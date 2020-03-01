package es.ucm.fdi.physionet.models;

import es.ucm.fdi.physionet.models.enums.UserRole;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {
    // TODO: Detalles del médico
    // TODO: Detalles del paciente

    private Long id;

    private String name;
    private String surname;
    private String username;
    private String password;

    private List<Messages> messages;
    private List<Appointment> appointments;

    private UserRole role;

    public User() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @OneToMany(targetEntity = Messages.class)
    public List<Messages> getMessages() {
        return messages;
    }

    public void setMessages(List<Messages> messages) {
        this.messages = messages;
    }

    @OneToMany(targetEntity = Messages.class)
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
