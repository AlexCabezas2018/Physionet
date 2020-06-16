/*
 * Entidad usuario, que contiene toda la información de todos los usuarios de la aplicación. Esta entidad se utilizará
 * para la gestión de usuarios dentro de la aplicación.
*/

package es.ucm.fdi.physionet.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import es.ucm.fdi.physionet.model.enums.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * A user; can be an Admin, a User, or a Moderator
 *
 * Users can log in and send each other messages.
 *
 * @author mfreire
 */
@Entity
@NamedQueries({
		@NamedQuery(name="AllUsers",
	        query="SELECT u FROM User u"),
        @NamedQuery(name="User.byUsername",
                query="SELECT u FROM User u "
                        + "WHERE u.username = :username AND u.enabled = 1"),
        @NamedQuery(name="User.hasUsername",
                query="SELECT COUNT(u) "
                        + "FROM User u "
                        + "WHERE u.username = :username"),
        @NamedQuery(name="User.byRole",
        query="SELECT u FROM User u WHERE u.roles = :role")
})

public class User {

    private static Logger log = LogManager.getLogger(User.class);
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User() {
		super();
	}

	public User(String username, String roles, String name, String surname) {
		super();
		this.username = username;
		this.roles = roles;
		this.name = name;
		this.surname = surname;
	}

	// do not change these fields
    private long id;
    private String username;
    private String password;
    private String roles; // split by ',' to separate roles
    private byte enabled;

    /**
     * Checks whether this user has a given role.
     * @param role to check
     * @return true iff this user has that role.
     */
    public boolean hasRole(UserRole role) {
        String roleName = role.name();
        return Arrays.stream(roles.split(","))
                .anyMatch(r -> r.equals(roleName));
    }

    public void setFullName(){

    }

    // application-specific fields
    private String name;
    private String surname;
    private long freeDaysLeft;

    private List<Message> sent = new ArrayList<>();
    private List<Message> received = new ArrayList<>();

    private List<Appointment> doctorAppointments = new ArrayList<>();
    private List<Appointment> patientAppointments = new ArrayList<>();

    private List<Absence> absences = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password to an encoded value.
     * You can generate encoded passwords using {@link #encodePassword}.
     * call only with encoded passwords - NEVER STORE PLAINTEXT PASSWORDS
     * @param encodedPassword to set as user's password
     */
    public void setPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * Tests a raw (non-encoded) password against the stored one.
     * @param rawPassword to test against
     * @return true if encoding rawPassword with correct salt (from old password)
     * matches old password. That is, true iff the password is correct
     */
    public boolean passwordMatches(String rawPassword) {
        return encoder.matches(rawPassword, this.password.split("}")[1]);
    }

    /**
     * Encodes a password, so that it can be saved for future checking. Notice
     * that encoding the same password multiple times will yield different
     * encodings, since encodings contain a randomly-generated salt.
     * @param rawPassword to encode
     * @return the encoded password (typically a 60-character string)
     * for example, a possible encoding of "test" is
     * $2y$12$XCKz0zjXAP6hsFyVc8MucOzx6ER6IsC1qo5zQbclxhddR1t6SfrHm
     */
    public static String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public byte getEnabled() {
        return enabled;
    }

    public void setEnabled(byte enabled) {
        this.enabled = enabled;
    }

    @OneToMany(targetEntity = Message.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    @JsonManagedReference
    public List<Message> getSent() {
        return sent;
    }

    public void setSent(List<Message> sent) {
        this.sent = sent;
    }

    @OneToMany(targetEntity = Message.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id")
    @JsonManagedReference
    public List<Message> getReceived() {
        return received;
    }

    public void setReceived(List<Message> received) {
        this.received = received;
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

    @OneToMany(targetEntity = Appointment.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    public List<Appointment> getPatientAppointments() {
        return patientAppointments;
    }

    public void setPatientAppointments(List<Appointment> appointments) {
        this.patientAppointments = appointments;
    }

    @OneToMany(targetEntity = Appointment.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    public List<Appointment> getDoctorAppointments() {
        return doctorAppointments;
    }

    @OneToMany(targetEntity = Absence.class, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    public List<Absence> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
    }

    public void setDoctorAppointments(List<Appointment> appointments) {
        this.doctorAppointments = appointments;
    }

    public long getFreeDaysLeft() {
        return freeDaysLeft;
    }

    public void setFreeDaysLeft(long freeDaysLeft) {
        this.freeDaysLeft = freeDaysLeft;
    }

    public String fullName() {
        return String.join(" ", name, surname);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                ", enabled=" + enabled +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", freeDaysLeft=" + freeDaysLeft +
                ", sent=" + sent +
                ", received=" + received +
                '}';
    }
}

