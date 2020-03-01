package es.ucm.fdi.physionet.models;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Appointment {
    private Long id;

    @ManyToOne(targetEntity = User.class)
    private User medic;

    @ManyToOne(targetEntity = User.class)
    private User patient;

    private String motive;
    private String location;
    private String details;

    private Date date;

    public Appointment() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

}
