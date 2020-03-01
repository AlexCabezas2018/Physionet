package es.ucm.fdi.physionet.models;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Appointment {
    private Long id;

    private User medic;
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

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    public User getMedic() {
        return medic;
    }

    public void setMedic(User medic) {
        this.medic = medic;
    }

    @ManyToOne(targetEntity = User.class)
    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public String getMotive() {
        return motive;
    }

    public void setMotive(String motive) {
        this.motive = motive;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
