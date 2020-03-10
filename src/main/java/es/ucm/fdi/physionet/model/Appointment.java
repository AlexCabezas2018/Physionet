/*
* Esta entidad representa una cita médica en la aplicación. La tabla almacenará dichas citas. La entidad se usará para
* gestión de las citas en la aplicación.
*/

package es.ucm.fdi.physionet.model;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class Appointment {
    private long id;

    private User doctor;
    private User patient;

    private String motive;
    private String location;
    private String details;

    private ZonedDateTime date;

    public Appointment() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", doctor=" + doctor +
                ", patient=" + patient +
                ", motive='" + motive + '\'' +
                ", location='" + location + '\'' +
                ", details='" + details + '\'' +
                ", date=" + date +
                '}';
    }
}
