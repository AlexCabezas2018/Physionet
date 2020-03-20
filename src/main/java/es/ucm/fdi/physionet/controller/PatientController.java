package es.ucm.fdi.physionet.controller;

import java.nio.channels.SeekableByteChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.physionet.model.Absence;
import es.ucm.fdi.physionet.model.Message;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private static Logger log = LogManager.getLogger(PatientController.class);

    private EntityManager entityManager;

    @Autowired
    private HttpSession session;

    public PatientController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GetMapping("")
    public String appointments(Model model) {
        log.debug("Hemos entrado a la ventana de citas pendientes");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "patient-appointments";
    }

    @GetMapping("/messages")
    public String menssageView( Model model) {
        log.debug("Hemos entrado en la vista de mensajes");
        User sessionUser = (User) session.getAttribute("u");
        ArrayList<String> receivedMessages = new ArrayList<String>();
        ArrayList<Message> messages = new ArrayList<Message>();

        for (Message m : sessionUser.getReceived()) 
            if(!receivedMessages.contains(m.getSender().getUsername()))
                receivedMessages.add(m.getSender().getUsername());
    
        setDefaultModelAttributes(model);
        model.addAttribute("user", sessionUser);
        model.addAttribute("receivedMessages", receivedMessages);
        return "messages-view";
    }

    @GetMapping("/messagesConversation")
    public String menssageViewConversation(@RequestParam String username, Model model) {
        log.debug("Hemos entrado en la vista de una conversacion");
        User sessionUser = (User) session.getAttribute("u");
        ArrayList<String> receivedMessages = new ArrayList<String>();
        ArrayList<Message> messages = new ArrayList<Message>();

        for(Message m : sessionUser.getReceived()) 
            if(!receivedMessages.contains(m.getSender().getUsername()))
                receivedMessages.add(m.getSender().getUsername());

        for(Message se : sessionUser.getSent())
            if(se.getRecipient().getUsername().equals(username)) 
                messages.add(se);
        for(Message re : sessionUser.getReceived()) 
            if(re.getSender().getUsername().equals(username)) 
                messages.add(re);   
              
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

    @PostMapping("/messagesConversation")
    @Transactional
    public String createAbsence(@RequestParam String textoMensaje, @RequestParam String username, Model model) {
        Message mess = new Message();
        log.info("Attempting to create an message with parameters={}", textoMensaje,username);
        User sessionUser = (User) session.getAttribute("u");
        ArrayList<User> users = (ArrayList<User>)entityManager.createNamedQuery("User.byUsername").setParameter("username", username).getResultList();
        User addreserUser = users.get(0);
        mess.setDateSent(LocalDateTime.now());
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

    private void setDefaultModelAttributes(Model model) {
        User sessionUser = (User) session.getAttribute("u");
        model.addAttribute("role", UserRole.PATIENT.toString());
        model.addAttribute("user", sessionUser);
    }
}
