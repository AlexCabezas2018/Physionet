package es.ucm.fdi.physionet.controller;

import es.ucm.fdi.physionet.model.Absence;
import es.ucm.fdi.physionet.model.util.Queries;
import es.ucm.fdi.physionet.model.enums.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import java.util.List;

@Controller
@RequestMapping("/absences")
public class AbsenceController {

    private static Logger log = LogManager.getLogger(RootController.class);

    @Autowired
    private EntityManager entityManager;

    @GetMapping("")
    @Transactional
    public String getAllAbsences(Model model) {
        log.info("Attempting to get all absences");
        // TODO: Check if the role is Doctor and return 403 if it is not.
        // FIXME: Error when Jackson tries to map the list fields of an user. Ask to teacher. Once the error is fixed, @JsonIgnore fields must be removed from the user entity

        List<Absence> absences = (List<Absence>)entityManager.createNamedQuery(Queries.GET_ALL_ABSENCES).getResultList();


        model.addAttribute("role", UserRole.DOCTOR);
        model.addAttribute("absences", absences);

        return "absences-view";
    }





}
