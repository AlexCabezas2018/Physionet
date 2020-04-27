package es.ucm.fdi.physionet.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    private static Logger log = LogManager.getLogger(RootController.class);

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/error")
    public String error() {
        log.debug("Ha ocurrido un error");
        return "error";
    }
}
