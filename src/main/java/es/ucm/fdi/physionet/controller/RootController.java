package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.util.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import java.util.List;

@Controller
public class RootController {
    private static final Logger log = LogManager.getLogger(RootController.class);

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/error")
    public String error() {
        log.debug("ERROR");
        return "error";
    }

    @PostMapping("/checkCredentials")
    @Transactional
    @ResponseBody
    public boolean checkCredentials(@RequestParam String username, @RequestParam String password) {
        List<User> users = entityManager.createNamedQuery(Queries.GET_USER_BY_USERNAME)
                .setParameter("username", username).getResultList();

        User u = users.isEmpty() ? null : users.get(0);
        return u != null && u.passwordMatches(password);
    }
}
