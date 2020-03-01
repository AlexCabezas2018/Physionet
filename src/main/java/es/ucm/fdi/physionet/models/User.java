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

    @OneToMany
    private List<Messages> messages;

    @OneToMany
    private List<Appointment> appointments;

    private UserRole role;

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
}
