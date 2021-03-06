package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.controller.util.ControllerUtils;
import es.ucm.fdi.physionet.model.Absence;
import es.ucm.fdi.physionet.model.Appointment;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.AbsenceStatus;
import es.ucm.fdi.physionet.model.enums.ServerMessages;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;


@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private static final Logger log = LogManager.getLogger(DoctorController.class);

    @Autowired
    private EntityManager entityManager;

	@Autowired
    private MessagesController messagesController;

	@Autowired
    private ControllerUtils utils;

    @GetMapping("")
    @Transactional
    public String appointments(Model model) {
        User u = utils.getFreshSessionUser();
        utils.setDefaultModelAttributes(model);
        setAppointmentsOfUser(u, model);
        return "doctor-appointments";
    }

    @GetMapping("/appointment")
    @Transactional
    public String getAllAppointments(@RequestParam long id, Model model) {
        User u = utils.getFreshSessionUser();
        log.debug("Hemos entrado en la vista de una conversacion");
        utils.setDefaultModelAttributes(model);
        setAppointmentsOfUser(u, model);
        Appointment app = entityManager.find(Appointment.class, id);
        model.addAttribute("actualAppointment", app);
        return "doctor-appointments";
    }

    @PostMapping("/appointment")
    @Transactional
    public String updateAppointment(@RequestParam long id, @ModelAttribute("appointment") Appointment appointment, Model model) {
        log.info("Attempting to update an appointment with recommendation={}", appointment.getRecommendations());
        User sessionUser = utils.getFreshSessionUser();
        Appointment actualAppointment = entityManager.find(Appointment.class, id);

        if(actualAppointment.getDoctor().getId() != sessionUser.getId()) return "doctor-appointments";

        actualAppointment.setRecommendations(appointment.getRecommendations());
        entityManager.persist(actualAppointment);
        utils.setDefaultModelAttributes(model);
        setAppointmentsOfUser(sessionUser, model);
        model.addAttribute("actualAppointment", actualAppointment);
        return "doctor-appointments";
    }

    @PostMapping("/finalizeAppointment/{id}")
    @Transactional
    @ResponseBody
    public Map<String, String>  finalizeAppointment(@PathVariable long id) {
        log.info("Attempting to finalize appointment with id: {}", id);
        Map<String, String> response = new HashMap<>();
        Appointment actualAppointment = entityManager.find(Appointment.class, id);
        User currentUser = utils.getFreshSessionUser();

        if (actualAppointment != null) {
            boolean canFinalize = currentUser.hasRole(UserRole.DOCTOR) &&
                    actualAppointment.getDoctor().getId() == currentUser.getId();

            if(!canFinalize) {
                log.warn("User has no permission to finalize appointment");
                log.warn("Current user is the following: {}", currentUser.toString());
                response.put("errorM", ServerMessages.APPOINTMENT_FINALIZED_ERROR.getPropertyName());

                return response;
            }

            actualAppointment.setIsFinalized(true);
            if (actualAppointment.getRecommendations().equals("")){
                actualAppointment.setRecommendations("Sin recomendaciones");
            }
            entityManager.persist(actualAppointment);
            response.put("successM", ServerMessages.APPOINTMENT_FINALIZED_SUCCESS.getPropertyName());
            return response;
        }
        response.put("errorM", ServerMessages.APPOINTMENT_FINALIZED_ERROR.getPropertyName());

        return response;
    }

    @GetMapping("/history/{userId}")
    public String patientHistory(@PathVariable String userId, Model model) {
        User user = entityManager.find(User.class, Long.parseLong(userId));

        List<Appointment> finalizedAppointments = utils.getFinalizedAppointments(user);
        model.addAttribute("appointments", finalizedAppointments);
        model.addAttribute("patientName", user.fullName());

        utils.setDefaultModelAttributes(model);

        return "patient-history";
    }

    @GetMapping("/messages")
    public String messageView(Model model) {
        return messagesController.messagesView(model, UserRole.DOCTOR);
    }

    @GetMapping("/messagesConversation")
    public String messageViewConversation(@RequestParam String username, Model model) {
        return messagesController.messageViewConversation(model, username, UserRole.DOCTOR);
    }

    @PostMapping("/messagesConversation")
    @Transactional
    public String addMessage(@RequestParam String messageText, @RequestParam String username, Model model) {
        return messagesController.addMessage(model, messageText, username, UserRole.DOCTOR);
    }

    @GetMapping("/absences")
    @Transactional
    public String absences(Model model) {
        log.info("Attempting to get all absences");
        return getAllAbsencesView(model);
    }

    @PostMapping("/absences")
    @Transactional
    public String createAbsence(@ModelAttribute("absence") Absence absence, Model model) {
        log.info("Attempting to create an absence with parameters={}", absence);
        User sessionUser = utils.getFreshSessionUser();
        absence.setUser(sessionUser);
        absence.setStatus(AbsenceStatus.PROPOSAL);

        long difference = DAYS.between(absence.getDateFrom(), absence.getDateTo());
        if (difference > sessionUser.getFreeDaysLeft()) {
            model.addAttribute("errorMessage", ServerMessages.ABSENCE_TO_LONG.getPropertyName());
            return getAllAbsencesView(model);
        }

        List<Appointment> filteredAppointments = getAppointmentsByUserAndDates(sessionUser,
                absence.getDateFrom().atStartOfDay(ZoneId.systemDefault()),
                absence.getDateTo().atStartOfDay(ZoneId.systemDefault()));

        if (filteredAppointments.size() != 0) {
            model.addAttribute("errorMessage", ServerMessages.APPOINTMENTS_IN_ABSENCE.getPropertyName());
            return getAllAbsencesView(model);
        }

        boolean absencesOverlapping = areAbsencesOverlapping(sessionUser, absence);

        if(absencesOverlapping) {
            model.addAttribute("errorMessage", ServerMessages.ABSENCE_IS_OVERLAPPING.getPropertyName());
            return getAllAbsencesView(model);
        }

        entityManager.persist(absence);

        sessionUser.setFreeDaysLeft(sessionUser.getFreeDaysLeft() - difference);
        model.addAttribute("successMessage", ServerMessages.ABSENCE_ADDED_SUCCESS.getPropertyName());

        log.info("Created absence with id={}", absence.getId());

        return getAllAbsencesView(model);
    }

    @PostMapping("/absence/delete/{id}")
    @Transactional
    @ResponseBody
    public Map<String, String> deleteAbsence(@PathVariable String id) {
        log.info("Attempting to delete absence with id: {}", id);

        Map<String, String> response = new HashMap<>();
        User sessionUser = utils.getFreshSessionUser();

        List<Absence> filteredAbsences = entityManager.createNamedQuery(Queries.GET_ABSENCE_BY_USER_AND_ID, Absence.class)
                .setParameter("user", sessionUser)
                .setParameter("id", Long.parseLong(id)).getResultList();

        if (filteredAbsences.isEmpty()) {
            response.put("errorMessage", ServerMessages.ABSENCE_IS_NOT_FROM_USER.getPropertyName());
            return response;
        }

        Absence absenceToDelete = entityManager.find(Absence.class, Long.valueOf(id));
        long difference = DAYS.between(absenceToDelete.getDateFrom(), absenceToDelete.getDateTo());

        sessionUser.setFreeDaysLeft(sessionUser.getFreeDaysLeft() + difference);
        entityManager.remove(absenceToDelete);

        response.put("successMessage", ServerMessages.ABSENCE_DELETED_SUCCESS.getPropertyName());
        response.put("freeDaysLeft", String.valueOf(sessionUser.getFreeDaysLeft()));
        return response;
    }

    private String getAllAbsencesView(Model model) {
        List<Absence> absences = entityManager.createNamedQuery(Queries.GET_ALL_ABSENCES, Absence.class).getResultList();
        log.debug("The following absences were obtained: {}", absences);

        utils.setDefaultModelAttributes(model);
        model.addAttribute("absence", new Absence());
        model.addAttribute("absences", Absence.asTransferObjects(absences));

        return "absences-view";
    }

    private void setAppointmentsOfUser(User actualUser, Model model) {
        ZonedDateTime startDay = ZonedDateTime.now().withHour(0).withMinute(0);
        ZonedDateTime endDay = ZonedDateTime.now().withHour(23).withMinute(59);

        List<Appointment> appointments = getAppointmentsByUserAndDates(actualUser, startDay, endDay);

        Hibernate.initialize(actualUser.getDoctorAppointments());
        model.addAttribute("appointments", appointments);
    }

    private List<Appointment> getAppointmentsByUserAndDates(User user, ZonedDateTime dateFrom, ZonedDateTime dateTo) {
        return entityManager.createNamedQuery(Queries.GET_APPOINTMENTS_BY_DOCTOR_BETWEEN_DATES, Appointment.class)
                .setParameter("now", dateFrom)
                .setParameter("endDay", dateTo)
                .setParameter("doc", user)
                .getResultList();
    }

    private boolean areAbsencesOverlapping(User user, Absence absenceToCheck) {
        List<Absence> absences = entityManager.createNamedQuery(Queries.GET_ALL_ABSENCES_BY_USER, Absence.class)
                .setParameter("user", user)
                .getResultList();

        return absences.stream().anyMatch(a -> absenceToCheck.getDateFrom().minusDays(1).isBefore(a.getDateTo())
                && absenceToCheck.getDateTo().plusDays(1).isAfter(a.getDateFrom()));
    }
}

