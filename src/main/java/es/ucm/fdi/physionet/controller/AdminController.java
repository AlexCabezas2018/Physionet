package es.ucm.fdi.physionet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private EntityManager entityManager;

    public AdminController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
