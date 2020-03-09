package es.ucm.fdi.physionet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private EntityManager entityManager;

    public PatientController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
