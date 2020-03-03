/*
 * Entidad usuario, que contiene toda la información de todos los usuarios de la aplicación. Esta entidad se utilizará
 * para la gestión de usuarios dentro de la aplicación.
*/

package es.ucm.fdi.physionet.model;

import es.ucm.fdi.physionet.model.enums.UserRole;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    // TODO: Detalles del médico
    // TODO: Detalles del paciente

    private long id;

    private String name;
    private String surname;
    private String username;
    private String password;

    private List<Message> recievedMessages = new ArrayList<>();
    private List<Message> sentMessages = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();

    private UserRole role;
    private ZonedDateTime createdAt;

    public User() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(targetEntity = Message.class)
    @JoinColumn(name = "user_from_id")
    public List<Message> getRecievedMessages() {
        return recievedMessages;
    }

    public void setRecievedMessages(List<Message> recievedMessages) {
        this.recievedMessages = recievedMessages;
    }

    @OneToMany(targetEntity = Message.class)
    @JoinColumn(name = "user_to_id")
    public List<Message> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<Message> sentMessages) {
        this.sentMessages = sentMessages;
    }

    @OneToMany(targetEntity = Appointment.class)
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
