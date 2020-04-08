/*
* Esta entidad representa una cita médica en la aplicación. La tabla almacenará dichas citas. La entidad se usará para
* gestión de las citas en la aplicación.
*/

package es.ucm.fdi.physionet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@NamedQueries({
    @NamedQuery(name="allAppointments",
        query="SELECT a FROM Appointment a"),
    @NamedQuery(name="appointments",
        query = "SELECT a FROM Appointment a WHERE date BETWEEN :now AND :endDay ORDER BY date ASC")
})
public class Appointment {
    private long id;

    private User doctor;
    private User patient;

    private String motive;
    private String location;
    private String details;

    private ZonedDateTime date;

    public Appointment() {}

    public Appointment(User doctor, User patient, String motive, String location, String details, ZonedDateTime date) {
        this.doctor = doctor;
        this.patient = patient;
        this.motive = motive;
        this.location = location;
        this.details = details;
        this.date = date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    @JsonBackReference
    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    @ManyToOne(targetEntity = User.class)
    @JsonBackReference
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
                ", motive='" + motive + '\'' +
                ", location='" + location + '\'' +
                ", details='" + details + '\'' +
                ", date=" + date +
                '}';
    }
}
