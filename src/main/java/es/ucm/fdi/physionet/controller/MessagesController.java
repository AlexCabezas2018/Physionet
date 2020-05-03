package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.controller.util.ControllerUtils;
import es.ucm.fdi.physionet.model.Message;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

@Component
public class MessagesController {

    private static final Logger log = LogManager.getLogger(PatientController.class);

    @Autowired
    EntityManager entityManager;

    @Autowired
    private ControllerUtils utils;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public String messagesView(Model model, UserRole role) {
        log.debug("Hemos entrado en la vista de mensajes");
        User sessionUser = utils.getFreshSessionUser();

        HashMap<String, Integer> receivedMessages = messageUsers(sessionUser);

        utils.setDefaultModelAttributes(model, role);
        model.addAttribute("user", sessionUser);
        model.addAttribute("receivedMessages", receivedMessages);
        return "messages-view";
    }

    public String messageViewConversation(Model model, String username, UserRole role) {
        log.debug("Hemos entrado en la vista de una conversacion");
        User sessionUser = utils.getFreshSessionUser();

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
        messages.sort(Comparator.comparing(Message::getDateSent));
        utils.setDefaultModelAttributes(model, role);

        model.addAttribute("user", sessionUser);
        model.addAttribute("usernameAddresser", username);
        model.addAttribute("conversation", messages);
        model.addAttribute("receivedMessages", receivedMessages);

        return "messages-view";
    }

    public String addMessage(Model model, String messageText, String username, UserRole role) {
        Message mess = new Message();
        log.info("Attempting to create an message to user: {}", username);

        ArrayList<User> users = (ArrayList<User>) entityManager.createNamedQuery(Queries.GET_USER_BY_USERNAME).setParameter("username", username).getResultList();

        mess.setDateSent(LocalDateTime.now());
        mess.setText(messageText);
        mess.setDateRead(null);

        User addresseeUser = users.get(0);
        mess.setRecipient(addresseeUser);
        addresseeUser.getReceived().add(mess);

        User sessionUser = utils.getFreshSessionUser();
        mess.setSender(sessionUser);
        sessionUser.getSent().add(mess);

        entityManager.persist(mess);
        entityManager.flush();

        log.info("Created message with id={}", mess.getId());

        // Ojo: esto es solo una demo. La plantilla usa un mejor sistema para escapar nombres
        messagingTemplate.convertAndSend(
                "/user/"+addresseeUser.getUsername()+"/queue/updates",
                "{\"from\": \"" + sessionUser.getUsername() + "\"}");

        return messageViewConversation(model, username, role);
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
}
