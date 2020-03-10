package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.model.Absence;
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
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private static Logger log = LogManager.getLogger(DoctorController.class);

    private EntityManager entityManager;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public DoctorController(EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public String appoinments(Model model) {
        log.debug("Hemos entrado a la vista de citas para el día de hoy");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "medic-appointments";
    }

    @GetMapping("/absences")
    @Transactional
    public String getAllAbsences(Model model) {
        log.info("Attempting to get all absences");
        // FIXME: Error when Jackson tries to map the list fields of an user. Ask to teacher. Once the error is fixed, @JsonIgnore fields must be removed from the user entity

        return getAllAbsencesView(model);
    }

    @PostMapping("/absences")
    @Transactional
    public String createAbsence(@ModelAttribute("absence") Absence absence, Model model) {
        log.info("Attempting to create an absence with parameters: {}", absence);
        entityManager.persist(absence);

        log.info("Created absence with id={}", absence.getId());

        return getAllAbsencesView(model);
    }

    private String getAllAbsencesView(Model model) {
        List<Absence> absences = (List<Absence>)entityManager.createNamedQuery(Queries.GET_ALL_ABSENCES).getResultList();

        model.addAttribute("role", UserRole.DOCTOR);
        model.addAttribute("absence", new Absence());
        model.addAttribute("absences", absences);

        return "absences-view";
    }
}

