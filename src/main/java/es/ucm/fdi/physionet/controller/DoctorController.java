package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.model.Absence;
import es.ucm.fdi.physionet.model.Message;
import es.ucm.fdi.physionet.model.User;
import es.ucm.fdi.physionet.model.enums.UserRole;
import es.ucm.fdi.physionet.model.util.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private static Logger log = LogManager.getLogger(DoctorController.class);

    @Autowired
    private HttpSession session;

    @Autowired
    private EntityManager entityManager;

    @GetMapping("")
    public String appointments(Model model) {
        log.info("Attempting to get all appointments for user={}", session.getAttribute("u").toString());
        setDefaultModelAttributes(model);
        return "doctor-appointments";
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

        entityManager.persist(absence);
        log.info("Created absence with id={}", absence.getId());

        return getAllAbsencesView(model);
    }

    @GetMapping("/messages")
    public String menssageView(Model model) {
        log.debug("Hemos entrado en la vista de mensajes");
        setDefaultModelAttributes(model);
        return "messages-view";
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
}

