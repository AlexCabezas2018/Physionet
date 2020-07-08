package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.controller.util.ControllerUtils;
import es.ucm.fdi.physionet.model.Message;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        utils.setDefaultModelAttributes(model);
        model.addAttribute("receivedMessages", receivedMessages);
        return "messages-view";
    }

    @Transactional
    public String messageViewConversation(Model model, String username, UserRole role) {
        log.debug("Hemos entrado en la vista de una conversacion");
        User sessionUser = utils.getFreshSessionUser();

        List<Message> messages = entityManager.createNamedQuery(Queries.GET_MESSAGES_CONVERSATION)
                .setParameter("userFrom", sessionUser)
                .setParameter("userTo", username)
                .getResultList();


        for (Message m : sessionUser.getReceived()) {
            if (m.getSender().getUsername().equals(username) && m.getDateRead() == null) {
                m.setDateRead(LocalDateTime.now());
            }
        }

        entityManager.flush();

        HashMap<String, Integer> receivedMessages;

        receivedMessages = messageUsers(sessionUser);

        utils.setDefaultModelAttributes(model);

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

        JSONObject payload = new JSONObject();
        payload.put("from", sessionUser.getUsername());
        payload.put("content_type", "chat-message");
        payload.put("content", mess.getText());
        payload.put("dateSent", mess.getDateSent().toString());
        payload.put("hourSent", Integer.toString(mess.getDateSent().getHour()) );
        payload.put("minuteSent", Integer.toString(mess.getDateSent().getMinute()) );


        messagingTemplate.convertAndSend(
                String.format("/user/%s/queue/updates", addresseeUser.getUsername()),
                payload.toJSONString());

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