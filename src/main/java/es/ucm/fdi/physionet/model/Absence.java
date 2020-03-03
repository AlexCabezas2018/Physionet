/*
* Representa una ausencia de un usuario. Esto se usará para los cuadrantes de horarios entre los médicos.
*/

package es.ucm.fdi.physionet.model;

import es.ucm.fdi.physionet.model.enums.AbsenceReason;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Absence {

	private long id;
	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	private AbsenceReason reason;
	private String details;
	private User user;

	public Absence() {}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public ZonedDateTime getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(ZonedDateTime dateFrom) {
		this.dateFrom = dateFrom;
	}

	public ZonedDateTime getDateTo() {
		return dateTo;
	}

	public void setDateTo(ZonedDateTime dateTo) {
		this.dateTo = dateTo;
	}

	@ManyToOne(targetEntity = User.class)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AbsenceReason getReason() {
		return reason;
	}

	public void setReason(AbsenceReason reason) {
		this.reason = reason;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
