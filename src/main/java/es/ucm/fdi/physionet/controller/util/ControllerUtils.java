package es.ucm.fdi.physionet.controller.util;

import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

@Component
public class ControllerUtils {
    @Autowired
    EntityManager entityManager;

    @Autowired
    private HttpSession session;

    public void setDefaultModelAttributes(Model model, UserRole role) {
        User sessionUser = (User) session.getAttribute("u");
        sessionUser = entityManager.find(User.class, sessionUser.getId());
        model.addAttribute("role", role.toString());
        model.addAttribute("user", sessionUser);
    }

    public User getFreshSessionUser() {
        User sessionUser = (User) session.getAttribute("u");
        return entityManager.find(User.class, sessionUser.getId());
    }
}
