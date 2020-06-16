/*
* Esta entidad representa una cita médica en la aplicación. La tabla almacenará dichas citas. La entidad se usará para
* gestión de las citas en la aplicación.
*/

package es.ucm.fdi.physionet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import es.ucm.fdi.physionet.model.util.Queries;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@NamedQueries({
    @NamedQuery(name = Queries.GET_APPOINTMENTS_BY_DOCTOR_BETWEEN_DATES,
        query = "SELECT a FROM Appointment a WHERE date BETWEEN :now AND :endDay AND doctor = :doc ORDER BY date ASC"),
    @NamedQuery(name = Queries.GET_APPOINTMENTS_BY_PATIENT_BETWEEN_DATES,
            query = "SELECT a FROM Appointment a WHERE date BETWEEN :now AND :endDay AND patient = :patient ORDER BY date ASC"),
    @NamedQuery(name = Queries.GET_APPOINTMENTS_BY_PATIENT_AFTER_DATE,
            query = "SELECT a FROM Appointment a WHERE patient = :pat AND date > :date"),
    @NamedQuery(name = Queries.GET_FINALIZED_APPOINTMENTS_BY_PATIENT,
            query = "SELECT a FROM Appointment a WHERE a.patient = :pat AND a.isFinalized = true")
})
public class Appointment {
    private long id;

    private User doctor;
    private User patient;

    private String motive;
    private String location;
    private String details;
    private String recommendations;

    private ZonedDateTime date;

    private boolean isFinalized;

    public Appointment() {}

    public Appointment(User doctor, User patient, String motive, String location, String details, String recommendations, ZonedDateTime date, boolean isFinalized) {
        this.doctor = doctor;
        this.patient = patient;
        this.motive = motive;
        this.location = location;
        this.details = details;
        this.recommendations = recommendations;
        this.date = date;
        this.isFinalized = isFinalized;
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

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public boolean getIsFinalized() {
        return isFinalized;
    }

    public void setIsFinalized(boolean finalized) {
        isFinalized = finalized;
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
                ", recommendations='" + recommendations + '\'' +
                ", date=" + date +
                ", isFinalized=" + isFinalized +
                '}';
    }
}
