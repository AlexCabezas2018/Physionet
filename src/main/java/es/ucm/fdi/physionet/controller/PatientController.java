package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.model.Appointment;
import es.ucm.fdi.physionet.model.Message;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private static Logger log = LogManager.getLogger(PatientController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private HttpSession session;


	@Autowired
	private SimpMessagingTemplate messagingTemplate;


    @GetMapping("")
    @Transactional
    public String appointments(Model model) {
        log.info("Attempting to get all appointments");
        setDefaultModelAttributes(model);

        return getAllAppointments(model);
    }

    @GetMapping("/appointment")
    @Transactional
    public String appointmentDetails(@RequestParam long id, Model model) {
        log.debug("Hemos entrado en la vista de una conversacion");
        setDefaultModelAttributes(model);
        
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
        setDefaultModelAttributes(model);
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
        setDefaultModelAttributes(model);

        List doctorsList = entityManager.createNamedQuery("User.byRole").setParameter("role", "DOCTOR").getResultList();

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
        log.debug("Hemos entrado en la vista de mensajes");
        User sessionUser = (User) session.getAttribute("u");
        sessionUser = entityManager.find(User.class, sessionUser.getId());

        HashMap<String, Integer> receivedMessages = messageUsers(sessionUser);

        setDefaultModelAttributes(model);
        model.addAttribute("user", sessionUser);
        model.addAttribute("receivedMessages", receivedMessages);
        return "messages-view";
    }

    @GetMapping("/messagesConversation")
    public String menssageViewConversation(@RequestParam String username, Model model) {
        log.debug("Hemos entrado en la vista de una conversacion");
        User sessionUser = (User) session.getAttribute("u");
        sessionUser = entityManager.find(User.class, sessionUser.getId());

        HashMap<String, Integer> receivedMessages;
        ArrayList<Message> messages = new ArrayList<Message>();

        for (Message se : sessionUser.getSent())
            if (se.getRecipient().getUsername().equals(username))
                messages.add(se);
        for (Message re : sessionUser.getReceived()) {
            if (re.getSender().getUsername().equals(username)) {
                messages.add(re);
            }
            if (re.getDateRead() == null) {
                re.setDateRead(LocalDateTime.now());
            }
        }

        receivedMessages = messageUsers(sessionUser);
        messages.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getDateSent().compareTo(o2.getDateSent());
            }
        });
        setDefaultModelAttributes(model);
        model.addAttribute("user", sessionUser);
        model.addAttribute("usernameAddresser", username);
        model.addAttribute("conversation", messages);
        model.addAttribute("receivedMessages", receivedMessages);
        return "messages-view";
    }

    private HashMap<String, Integer> messageUsers(User sessionUser) {
        HashMap<String, Integer> messageUsers = new HashMap<>();
        for (Message m : sessionUser.getReceived()) {
            if (!messageUsers.containsKey(m.getSender().getUsername())) {
                messageUsers.put(m.getSender().getUsername(), 0);
            }
            if (messageUsers.containsKey(m.getSender().getUsername()) && m.getDateRead() == null) {
                messageUsers.replace(m.getSender().getUsername(), messageUsers.get(m.getSender().getUsername()) + 1);
            }
        }
        return messageUsers;
    }

    @PostMapping("/messagesConversation")
    @Transactional
    public String addMessage(@RequestParam String textoMensaje, @RequestParam String username, Model model) {
        Message mess = new Message();
        log.info("Attempting to create an message with parameters={}", textoMensaje, username);
        User sessionUser = (User) session.getAttribute("u");
        sessionUser = entityManager.find(User.class, sessionUser.getId());

        ArrayList<User> users = (ArrayList<User>) entityManager.createNamedQuery("User.byUsername").setParameter("username", username).getResultList();
        User addreserUser = users.get(0);
        mess.setDateSent(LocalDateTime.now());
        mess.setSender(sessionUser);
        mess.setRecipient(addreserUser);
        mess.setText(textoMensaje);
        mess.setDateRead(null);
        sessionUser.getSent().add(mess);
        addreserUser.getReceived().add(mess);
        entityManager.persist(mess);
        entityManager.flush();
        log.info("Created message with id={}", mess.getId());

        // Ojo: esto es solo una demo. La plantilla usa un mejor sistema para escapar nombres
		messagingTemplate.convertAndSend(
            "/user/"+addreserUser.getUsername()+"/queue/updates", 
            "{\"from\": \"" + sessionUser.getUsername() + "\"}");

        return menssageViewConversation(username, model);
    }

    private void setDefaultModelAttributes(Model model) {
        User sessionUser = (User) session.getAttribute("u");
        model.addAttribute("role", UserRole.PATIENT.toString());
        model.addAttribute("user", sessionUser);
    }
}
