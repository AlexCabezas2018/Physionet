/*
* Representa una ausencia de un usuario. Esto se usará para los cuadrantes de horarios entre los médicos.
*/

package es.ucm.fdi.physionet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.ucm.fdi.physionet.model.enums.AbsenceReason;
import es.ucm.fdi.physionet.model.util.Queries;

import java.time.ZonedDateTime;

import javax.persistence.*;

@Entity
@NamedQueries({
		@NamedQuery(name = Queries.GET_ALL_ABSENCES, query = "select a from Absence a")
})
public class Absence {

	private long id;
	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	private AbsenceReason reason;
	private String details;
	private User user;

	public Absence() {}

	public Absence(ZonedDateTime dateFrom, ZonedDateTime dateTo, AbsenceReason reason, String details, User user) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.reason = reason;
		this.details = details;
		this.user = user;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	@JsonFormat(pattern="yyyy-MM-dd")
	public ZonedDateTime getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(ZonedDateTime dateFrom) {
		this.dateFrom = dateFrom;
	}

	@JsonFormat(pattern="yyyy-MM-dd")
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

	@Override
	public String toString() {
		return "Absence{" +
				"id=" + id +
				", dateFrom=" + dateFrom +
				", dateTo=" + dateTo +
				", reason=" + reason +
				", details='" + details + '\'' +
				", user=" + user +
				'}';
	}
}
