package es.ucm.fdi.physionet.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private static Logger log = LogManager.getLogger(PatientController.class);

    private EntityManager entityManager;

    public PatientController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GetMapping("")
    public String appointments(Model model) {
        log.debug("Hemos entrado a la ventana de citas pendientes");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "patient-appointments";
    }
}
