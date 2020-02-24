package es.ucm.fdi.physionet.controller;

import org.springframework.stereotype.Controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    private static Logger log = LogManager.getLogger(RootController.class);

    @GetMapping("/")
    public String rootGet() {
        log.debug("Hemos entrado en el método principal");
        return "hola-mundo";
    }

    @GetMapping("/messages")
    public String menssageView(Model model) {
        log.debug("Hemos entrado en la vista de mensajes");
        model.addAttribute("userName", "Fernando Jiménez");
        return "messages";
    }

    @GetMapping("/future-appointments-medic")
    public String futureAppointmentsMedic(Model model) {
        log.debug("Hemos entrado a la vista de citas para el día de hoy");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "medic-main-view";
    }
    
    @GetMapping("/historial")
    public String historialView(Model model) {
        log.debug("Hemos entrado a la ventana de histoarial de citas");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "historial";
    }
    
     @GetMapping("/citas-pendientes")
    public String citasView(Model model) {
        log.debug("Hemos entrado a la ventana de citas pendientes");
        model.addAttribute("patientUserName", "Elena Martinez");
        return "citas-pendientes";
    }

    @GetMapping("/perfil")
    public String menssageView() {
        log.debug("Hemos entrado en el perfil de un paciente");
        return "perfil-pac";
    }
    @GetMapping("/admin")
    public String adminView() {
        log.debug("Hemos entrado en la vista de admin");
        return "admin";
    }

    @GetMapping("/calendar")
    public String calendarView(Model model) {
        log.debug("Hemos entrado en la vista del calendario");
        model.addAttribute("missingDays", 3);
        return "calendar";
    }

}
