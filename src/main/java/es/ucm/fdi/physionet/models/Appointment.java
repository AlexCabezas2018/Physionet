package es.ucm.fdi.physionet.models;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Appointment {
    private Long id;

    @ManyToOne
    private User medic;

    @ManyToOne
    private User patient;

    private String motive;
    private String location;
    private String details;

    private Date date;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

}
