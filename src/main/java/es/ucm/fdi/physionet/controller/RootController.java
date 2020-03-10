package es.ucm.fdi.physionet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.EntityManager;

@Controller
public class RootController {
    private static Logger log = LogManager.getLogger(RootController.class);

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/messages")
    public String menssageView(Model model) {
        log.debug("Hemos entrado en la vista de mensajes");
        model.addAttribute("userName", "Fernando Jiménez");
        return "messages-view";
    }

    @GetMapping("/historial")
    public String historialView(Model model) {
        log.debug("Hemos entrado a la ventana de histoarial de citas");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "patient-history";
    }

    @GetMapping("/patient-profile")
    public String patientProfile() {
        log.debug("Hemos entrado en el perfil de un paciente");
        return "patient-profile";
    }

    @GetMapping("/admin-pacient")
    public String adminPacientView() {
        log.debug("Hemos entrado en la vista de admin viendo el perfil de un paciente");
        return "admin-patient-view";
    }
    @GetMapping("/admin-doctor")
    public String adminDoctorView() {
        log.debug("Hemos entrado en la vista de admin viendo el perfil de un médico");
        return "admin-doctor-view";
    }
}
