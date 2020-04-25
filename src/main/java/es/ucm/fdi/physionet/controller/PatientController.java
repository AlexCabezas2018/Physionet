package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.controller.util.ControllerUtils;
import es.ucm.fdi.physionet.model.Appointment;
import es.ucm.fdi.physionet.model.User;
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
import javax.servlet.http.HttpSession;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private static Logger log = LogManager.getLogger(PatientController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private HttpSession session;

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
    public String appointmentDetails(@RequestParam long id, Model model) {
        log.debug("Hemos entrado en la vista de una conversacion");
        utils.setDefaultModelAttributes(model, UserRole.PATIENT);
        
        Appointment app = entityManager.find(Appointment.class, id);
        User u = (User) session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        List<Appointment> pendingApp = new ArrayList<Appointment>();
        ZonedDateTime today = ZonedDateTime.now();
        for(Appointment a : u.getPatientAppointments()){
            if(a.getDate().isAfter(today)){
                pendingApp.add(a);
            }
        }
        model.addAttribute("appointments", pendingApp);
        model.addAttribute("actualAppointment", app);
        return "patient-appointment-details";
    }

    @GetMapping("/todayapp")
    @Transactional
    public String todayAppointments(Model model) {
        User u = (User) session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());
        ZonedDateTime startDay = ZonedDateTime.now().withHour(0).withMinute(0);
        ZonedDateTime endDay = ZonedDateTime.now().withHour(23).withMinute(59);

        List<Appointment> appointments = new ArrayList<>();

        log.info("Attempting to get all appointments");
        utils.setDefaultModelAttributes(model, UserRole.PATIENT);
        for (Appointment a : u.getPatientAppointments()) {
            if (a.getDate().isBefore(endDay) && a.getDate().isAfter(startDay)) {
                appointments.add(a);
            }
        }

        model.addAttribute("appointments", appointments);
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
            response.put("successM", "Se ha cancelado la cita");
            return response;
        }
        response.put("errorM", "Fallo al cancelar la cita");
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
        User sessionUser = (User) session.getAttribute("u");
        sessionUser = entityManager.find(User.class, sessionUser.getId());

        List users = entityManager.createNamedQuery(Queries.GET_USER_BY_USERNAME).setParameter("username", doctor).getResultList();
        User doctorUser = (User) users.get(0);
        ZonedDateTime date2 = ZonedDateTime.parse(date + "T" + hour + ":00+02:00[Europe/London]");

        app.setDoctor(doctorUser);
        app.setDate(date2);
        app.setPatient(sessionUser);
        app.setMotive(motive);
        app.setDetails(details);
        app.setLocation("Sala 3");

        entityManager.persist(app);
        sessionUser.getPatientAppointments().add(app);

        log.info("Created app with id={}", app.getId());

        return getAllAppointments(model);
    }

    private String getAllAppointments(Model model) {
        User u = (User) session.getAttribute("u");

        u = entityManager.find(User.class, u.getId());
        log.debug("The following appointments were obtained: {}");
        utils.setDefaultModelAttributes(model, UserRole.PATIENT);

        List doctorsList = entityManager.createNamedQuery(Queries.GET_USER_BY_ROLE).setParameter("role", "DOCTOR").getResultList();

        List<Appointment> pendingApp = new ArrayList<Appointment>();
        ZonedDateTime today = ZonedDateTime.now();
        for(Appointment a : u.getPatientAppointments()){
            if(a.getDate().isAfter(today)){
                pendingApp.add(a);
            }
        }
        model.addAttribute("appointments", pendingApp);
        model.addAttribute("doctorsList", doctorsList);

        return "patient-appointments";
    }


    @GetMapping("/messages")
    public String menssageView(Model model) {
        return messagesController.messagesView(model, UserRole.PATIENT);
    }

    @GetMapping("/messagesConversation")
    public String menssageViewConversation(@RequestParam String username, Model model) {
        return messagesController.messageViewConversation(model, username, UserRole.PATIENT);
    }

    @PostMapping("/messagesConversation")
    @Transactional
    public String addMessage(@RequestParam String messageText, @RequestParam String username, Model model) {
        return messagesController.addMessage(model, messageText, username, UserRole.PATIENT);
    }
}
