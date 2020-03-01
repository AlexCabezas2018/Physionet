package es.ucm.fdi.physionet.models;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Absences {
	
	
	public enum Reason {
        SICKNESS,
        PERSONALAFFAIRS,
        HOLIDAYS,
        CHILDBIRTH,
        DEATHOFRElATIVE,
        MEDICALSICKLEAVE,  
        PREGNANCY,
    }
	
	private Long id;
	
	private Date absenceDate;

	private Reason reason;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	@ManyToOne
	private User user;
	
	public void setId(Long id) {
		this.id = id;
	}

	public Date getAbsenceDate() {
		return absenceDate;
	}

	public void setAbsenceDate(Date absenceDate) {
		this.absenceDate = absenceDate;
	}
	public Reason getReason() {
		return reason;
	}

	public void setReason(Reason reason) {
		this.reason = reason;
	}
}
