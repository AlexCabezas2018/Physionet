package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.model.Absence;
import es.ucm.fdi.physionet.model.Appointment;
import es.ucm.fdi.physionet.model.Message;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;
import es.ucm.fdi.physionet.model.util.ServerMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    @GetMapping("")
    @Transactional
    public String appointments(Model model) {
        User u = (User) session.getAttribute("u");

        u = entityManager.find(User.class, u.getId());
        
        log.info("Attempting to get all appointments for user={}", u);
        setDefaultModelAttributes(model);
        ZonedDateTime startDay = ZonedDateTime.now().withHour(0).withMinute(0);
        ZonedDateTime endDay = ZonedDateTime.now().withHour(23).withMinute(59);
        
        List<Appointment> today = new ArrayList<>();
        for (Appointment a : u.getDoctorAppointments()) {
            if (a.getDate().isBefore(endDay) && a.getDate().isAfter(startDay)) {
                today.add(a);
            }
        }

        model.addAttribute("appointments", today);
        return "doctor-appointments";
    }

    @GetMapping("/appointment")
    public String menssageViewConversation(@RequestParam long id, Model model) {
        log.debug("Hemos entrado en la vista de una conversacion");
        setDefaultModelAttributes(model);
        User sessionUser = (User) session.getAttribute("u");
        ZonedDateTime endDay = ZonedDateTime.now().withHour(23).withMinute(59);
        ArrayList<Appointment> appointments =  (ArrayList<Appointment>) entityManager.createNamedQuery("appointments")
                .setParameter("now", ZonedDateTime.now()).setParameter("endDay", endDay).getResultList();
        
        for (Appointment appointment : appointments) {
            if(appointment.getDoctor().getId() != sessionUser.getId())
                appointments.remove(appointment);
        }

        Appointment app = entityManager.find(Appointment.class,id);
        model.addAttribute("appointmentSingle", app);
        model.addAttribute("appointments", appointments);
        return "doctor-appointments";
    }

    @GetMapping("/messages")
    public String menssageView( Model model) {
        log.debug("Hemos entrado en la vista de mensajes");
        User sessionUser = (User) session.getAttribute("u");
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
        HashMap<String, Integer> receivedMessages;
        ArrayList<Message> messages = new ArrayList<>();

        for(Message se : sessionUser.getSent())
            if(se.getRecipient().getUsername().equals(username))
                messages.add(se);
        for(Message re : sessionUser.getReceived()) {
            if (re.getSender().getUsername().equals(username)) {
                messages.add(re);
            }
            if (re.getDateRead() == null){
                re.setDateRead(LocalDateTime.now());
            }
        }

        receivedMessages = messageUsers(sessionUser);

        messages.sort(Comparator.comparing(Message::getDateSent));

        setDefaultModelAttributes(model);
        model.addAttribute("user", sessionUser);
        model.addAttribute("usernameAddresser", username);
        model.addAttribute("conversation", messages);
        model.addAttribute("receivedMessages", receivedMessages);
        return "messages-view";
    }

    private HashMap<String, Integer> messageUsers(User sessionUser){
        HashMap<String, Integer> messageUsers = new HashMap<>();
        for (Message m : sessionUser.getReceived()) {
            if (!messageUsers.containsKey(m.getSender().getUsername())) {
                messageUsers.put(m.getSender().getUsername(), 0);
            }
            if (messageUsers.containsKey(m.getSender().getUsername()) && m.getDateRead() == null) {
                messageUsers.replace(m.getSender().getUsername(), messageUsers.get(m.getSender().getUsername())+1);
            }
        }
        return messageUsers;
    }

    @PostMapping("/messagesConversation")
    @Transactional
    public String addMessage(@RequestParam String textoMensaje, @RequestParam String username, Model model) {
        Message mess = new Message();
        log.info("Attempting to create an message with parameters={}", textoMensaje,username);
        User sessionUser = (User) session.getAttribute("u");
        ArrayList<User> users = (ArrayList<User>)entityManager.createNamedQuery("User.byUsername").setParameter("username", username).getResultList();
        User addreserUser = users.get(0);
        mess.setDateSent(LocalDateTime.now());
        mess.setDateRead(null);
        mess.setSender(sessionUser);
        mess.setRecipient(addreserUser);
        mess.setText(textoMensaje);
        sessionUser.getSent().add(mess);
        addreserUser.getReceived().add(mess);
        entityManager.persist(mess);
        entityManager.flush();
        log.info("Created message with id={}", mess.getId());
        return menssageViewConversation(username,model);
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

        absence.setUser(sessionUser);
        absence.setDateTo(absence.getDateTo().plusDays(1));

        long difference = DAYS.between(absence.getDateFrom(), absence.getDateTo());

        if(difference > sessionUser.getFreeDaysLeft()) {
            model.addAttribute("errorMessage", ServerMessages.ABSENCE_TO_LONG);
            return getAllAbsencesView(model);
        }

        List<Appointment> filteredAppointments = filterAppointmentByDate(sessionUser, absence);

        if(filteredAppointments.size() != 0) {
            model.addAttribute("errorMessage", ServerMessages.APPOINTMENTS_IN_ABSENCE);
            return getAllAbsencesView(model);
        }

        entityManager.persist(absence);

        sessionUser.setFreeDaysLeft(sessionUser.getFreeDaysLeft() - difference);
        model.addAttribute("successMessage", ServerMessages.ABSENCE_ADDED_SUCCESS);

        log.info("Created absence with id={}", absence.getId());

        return getAllAbsencesView(model);
    }

    private String getAllAbsencesView(Model model) {
        List<Absence> absences = (List<Absence>)entityManager.createNamedQuery(Queries.GET_ALL_ABSENCES).getResultList();
        log.debug("The following absences were obtained: {}", absences);

        setDefaultModelAttributes(model);
        model.addAttribute("absence", new Absence());
        model.addAttribute("absences", absences);

        return "absences-view";
    }

    private void setDefaultModelAttributes(Model model) {
        User sessionUser = (User) session.getAttribute("u");
        model.addAttribute("role", UserRole.DOCTOR.toString());
        model.addAttribute("user", sessionUser);
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

