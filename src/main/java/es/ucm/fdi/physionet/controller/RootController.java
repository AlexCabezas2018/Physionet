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
    public String rootGet() {
        log.debug("Hemos entrado en el método principal");
        return "hola-mundo";
    }

    @GetMapping("/messages")
    public String menssageView(Model model) {
        log.debug("Hemos entrado en la vista de mensajes");
        model.addAttribute("userName", "Fernando Jiménez");
        return "messages-view";
    }

    @GetMapping("/future-appointments-medic")
    public String futureAppointmentsMedic(Model model) {
        log.debug("Hemos entrado a la vista de citas para el día de hoy");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "medic-appointments";
    }
    
    @GetMapping("/historial")
    public String historialView(Model model) {
        log.debug("Hemos entrado a la ventana de histoarial de citas");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "patient-history";
    }
    
     @GetMapping("/citas-pendientes")
    public String citasView(Model model) {
        log.debug("Hemos entrado a la ventana de citas pendientes");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "patient-appointments";
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
    @GetMapping("/admin-medic")
    public String adminMedicView() {
        log.debug("Hemos entrado en la vista de admin viendo el perfil de un médico");
        return "admin-medic-view";
    }

    @GetMapping("/calendar")
    public String calendarView(Model model) {
        log.debug("Hemos entrado en la vista del calendario");
        model.addAttribute("missingDays", 3);
        return "absences-view";
    }

}
