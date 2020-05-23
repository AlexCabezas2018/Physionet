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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private static final Logger log = LogManager.getLogger(PatientController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessagesController messagesController;

    @Autowired
    private ControllerUtils utils;

    @GetMapping("")
    @Transactional
    public String appointments(Model model) {
        log.info("Attempting to get all appointments");
        utils.setDefaultModelAttributes(model, UserRole.PATIENT);

        return getAllAppointments(model);
    }

    @GetMapping("/appointment")
    @Transactional
    public String appointmentDetails(@RequestParam long id,@RequestParam boolean today ,Model model) {
        log.debug("Hemos entrado en la vista de una conversacion");
        utils.setDefaultModelAttributes(model, UserRole.PATIENT);
        
        Appointment app = entityManager.find(Appointment.class, id);
        User u = utils.getFreshSessionUser();
        
        if(today)
            model.addAttribute("appointments", getAppointmentsForToday(u));
        else
            model.addAttribute("appointments", getAppointmentsPending(u));
        
        model.addAttribute("today", today);
        model.addAttribute("actualAppointment", app);
        return "patient-appointment-details";
    }

    @GetMapping("/todayapp")
    @Transactional
    public String todayAppointments(Model model) {
        User u = utils.getFreshSessionUser();

        log.info("Attempting to get all appointments");
        utils.setDefaultModelAttributes(model, UserRole.PATIENT);
        
        model.addAttribute("today", true);
        model.addAttribute("appointments", getAppointmentsForToday(u));
        return "patient-appointments";
    }

    @PostMapping("/deleteappointment/{id}")
    @Transactional
    @ResponseBody
    public Map<String, String> deleteAppointment(@PathVariable long id) {
        log.info("Attempting to delete appointment with id: {}", id);

        Map<String, String> response = new HashMap<>();
        Appointment target = entityManager.find(Appointment.class, id);

        if (target != null) {
            entityManager.remove(target);
            response.put("successM", ServerMessages.APPOINTMENT_DELETED_SUCCESS.getPropertyName());
            return response;
        }
        response.put("errorM", ServerMessages.APPOINTMENT_DELETED_ERROR.getPropertyName());
        return response;
    }

    @PostMapping("")
    @Transactional
    public String createAppointment(@RequestParam String date, @RequestParam String hour,
                                    @RequestParam String doctor, @RequestParam String motive,
                                    @RequestParam String details,
                                    Model model) {
        log.info("Attempting to create an appointment with parameters");

        Appointment app = new Appointment();
        User sessionUser = utils.getFreshSessionUser();

        List<User> users = entityManager
            .createNamedQuery(Queries.GET_USER_BY_USERNAME, User.class)
            .setParameter("username", doctor)
            .getResultList();

        User doctorUser = users.get(0);
        LocalDate dateLocal = LocalDate.parse(date);
        ZonedDateTime date2 = ZonedDateTime.parse(date + "T" + hour + ":00+02:00[Europe/Madrid]");

        List<String> appointmentsLocations = Arrays.asList("Sala 1", "Sala 2", "Sala 3", "Sala 4");
        String appLocation = appointmentsLocations.get(new Random().nextInt(appointmentsLocations.size() - 1));

        List<Absence> filteredAbsences = entityManager.createNamedQuery(Queries.GET_ABSENCE_BY_USER_AND_DATE_BETWEEN_DATE_TO_AND_DATE_FROM)
                .setParameter("user", doctorUser)
                .setParameter("date", dateLocal)
                .getResultList();

        if (filteredAbsences.size() != 0) {
            model.addAttribute("errorM", ServerMessages.APPOINTMENT_BETWEEN_ABSENCE.getPropertyName());
            return getAllAppointments(model);
        }

        app.setDoctor(doctorUser);
        app.setDate(date2);
        app.setPatient(sessionUser);
        app.setMotive(motive);
        app.setDetails(details);
        app.setLocation(appLocation);
        app.setIsFinalized(false);

        entityManager.persist(app);
        sessionUser.getPatientAppointments().add(app);

        model.addAttribute("successM", ServerMessages.APPOINTMENT_ADDED_SUCCESS.getPropertyName());
        log.info("Created app with id={}", app.getId());

        return getAllAppointments(model);
    }
    
    
    private String getAllAppointments(Model model) {
        log.debug("Attempting to obtain all appointments");

        User u = utils.getFreshSessionUser();
        utils.setDefaultModelAttributes(model, UserRole.PATIENT);

        List<User> doctorsList = entityManager
            .createNamedQuery(Queries.GET_USER_BY_ROLE, User.class)
            .setParameter("role", "DOCTOR")
            .getResultList();

        model.addAttribute("today", false);
        model.addAttribute("appointments", getAppointmentsPending(u));
        model.addAttribute("doctorsList", doctorsList);

        return "patient-appointments";
    }

    @GetMapping("/messages")
    public String messageView(Model model) {
        return messagesController.messagesView(model, UserRole.PATIENT);
    }

    @GetMapping("/messagesConversation")
    public String messageViewConversation(@RequestParam String username, Model model) {
        return messagesController.messageViewConversation(model, username, UserRole.PATIENT);
    }

    @PostMapping("/messagesConversation")
    @Transactional
    public String addMessage(@RequestParam String messageText, @RequestParam String username, Model model) {
        return messagesController.addMessage(model, messageText, username, UserRole.PATIENT);
    }

    private List<Appointment> getAppointmentsForToday(User user) {
        ZonedDateTime startDay = ZonedDateTime.now().withHour(0).withMinute(0);
        ZonedDateTime endDay = ZonedDateTime.now().withHour(23).withMinute(59);

        return entityManager.createNamedQuery(Queries.GET_APPOINTMENTS_BY_PATIENT_BETWEEN_DATES)
                .setParameter("now", startDay)
                .setParameter("endDay", endDay)
                .setParameter("patient", user)
                .getResultList();
    }

    private List<Appointment> getAppointmentsPending(User user) {
        ZonedDateTime today = ZonedDateTime.now();
        return entityManager.createNamedQuery(Queries.GET_APPOINTMENTS_BY_PATIENT_AFTER_DATE)
                .setParameter("pat", user)
                .setParameter("date", today)
                .getResultList();
    }

}