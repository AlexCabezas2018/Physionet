package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.controller.util.ControllerUtils;
import es.ucm.fdi.physionet.model.Absence;
import es.ucm.fdi.physionet.model.Appointment;
import es.ucm.fdi.physionet.model.User;
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
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;


@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private static Logger log = LogManager.getLogger(DoctorController.class);

    @Autowired
    private HttpSession session;

    @Autowired
    private EntityManager entityManager;

	@Autowired
    private MessagesController messagesController;

	@Autowired
    private ControllerUtils utils;

    @GetMapping("")
    @Transactional
    public String appointments(Model model) {
        User u = (User) session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());

        log.info("Attempting to get all appointments for user={}", u);
        utils.setDefaultModelAttributes(model, UserRole.DOCTOR);
        setAppointmentsOfUser(u, model);
        return "doctor-appointments";
    }

    @GetMapping("/appointment")
    @Transactional
    public String getAllAppointments(@RequestParam long id, Model model) {
        User u = (User) session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        log.debug("Hemos entrado en la vista de una conversacion");
        utils.setDefaultModelAttributes(model, UserRole.DOCTOR);
        setAppointmentsOfUser(u, model);
        Appointment app = entityManager.find(Appointment.class, id);
        model.addAttribute("actualAppointment", app);
        return "doctor-appointments";
    }

    @GetMapping("/messages")
    public String menssageView(Model model) {
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
        User sessionUser = (User) session.getAttribute("u");
        sessionUser = entityManager.find(User.class, sessionUser.getId());    
        absence.setUser(sessionUser);
        absence.setDateTo(absence.getDateTo().plusDays(1));

        long difference = DAYS.between(absence.getDateFrom(), absence.getDateTo());

        if (difference > sessionUser.getFreeDaysLeft()) {
            model.addAttribute("errorMessage", ServerMessages.ABSENCE_TO_LONG.getPropertyName());
            return getAllAbsencesView(model);
        }

        List<Appointment> filteredAppointments = filterAppointmentByDate(sessionUser, absence);

        if (filteredAppointments.size() != 0) {
            model.addAttribute("errorMessage", ServerMessages.APPOINTMENTS_IN_ABSENCE.getPropertyName());
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
        User sessionUser = (User) session.getAttribute("u");
        sessionUser = entityManager.find(User.class, sessionUser.getId());

        List<Absence> filteredAbsences = sessionUser.getAbsences().stream()
                .filter(absence -> absence.getId() == Integer.valueOf(id))
                .collect(Collectors.toList());

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
        List absences = entityManager.createNamedQuery(Queries.GET_ALL_ABSENCES).getResultList();
        log.debug("The following absences were obtained: {}", absences);

        utils.setDefaultModelAttributes(model, UserRole.DOCTOR);
        model.addAttribute("absence", new Absence());
        model.addAttribute("absences", Absence.asTransferObjects(absences));

        return "absences-view";
    }

    private void setAppointmentsOfUser(User actualUser, Model model) {
        ZonedDateTime startDay = ZonedDateTime.now().withHour(0).withMinute(0);
        ZonedDateTime endDay = ZonedDateTime.now().withHour(23).withMinute(59);

        List<Appointment> appointments = new ArrayList<>();
        Hibernate.initialize(actualUser.getDoctorAppointments());
        for (Appointment a : actualUser.getDoctorAppointments()) {
            if (a.getDate().isBefore(endDay) && a.getDate().isAfter(startDay)) {
                appointments.add(a);
            }
        }

        model.addAttribute("appointments", appointments);
    }

    private List<Appointment> filterAppointmentByDate(User sessionUser, Absence absence) {
        return sessionUser.getDoctorAppointments()
                .stream()
                .filter(appointment -> {
                    LocalDate appointmentDay = appointment.getDate().toLocalDate();
                    int greaterThanDayFrom = appointmentDay.compareTo(absence.getDateFrom());
                    int lessThanDayTo = appointmentDay.compareTo(absence.getDateTo());

                    boolean greater = greaterThanDayFrom >= 0;
                    boolean lower = lessThanDayTo <= 0;

                    return greater && lower;
                })
                .collect(Collectors.toList());
    }
}

