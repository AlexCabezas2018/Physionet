package es.ucm.fdi.physionet.controller.util;

import es.ucm.fdi.physionet.model.Appointment;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@Component
public class ControllerUtils {
    @Autowired
    EntityManager entityManager;

    @Autowired
    private HttpSession session;

    public void setDefaultModelAttributes(Model model) {
        User sessionUser = (User) session.getAttribute("u");
        sessionUser = entityManager.find(User.class, sessionUser.getId());
        model.addAttribute("role", sessionUser.getRoles());
        model.addAttribute("user", sessionUser);
    }

    public User getFreshSessionUser() {
        User sessionUser = (User) session.getAttribute("u");
        return entityManager.find(User.class, sessionUser.getId());
    }

    public List<Appointment> getFinalizedAppointments(User user) {
        return entityManager.createNamedQuery(Queries.GET_FINALIZED_APPOINTMENTS_BY_PATIENT)
                .setParameter("pat", user)
                .getResultList();
    }
}
