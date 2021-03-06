/*
* Representa una ausencia de un usuario. Esto se usará para los cuadrantes de horarios entre los médicos.
*/

package es.ucm.fdi.physionet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.ucm.fdi.physionet.model.enums.AbsenceReason;
import es.ucm.fdi.physionet.model.enums.AbsenceStatus;
import es.ucm.fdi.physionet.model.util.Queries;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NamedQueries(value = {
		@NamedQuery(name = Queries.GET_ALL_ABSENCES, query = "select a from Absence a"),
		@NamedQuery(name = Queries.GET_ABSENCE_BY_USER_AND_ID, query = "select a from Absence a where a.user = :user and a.id = :id"),
		@NamedQuery(name = Queries.GET_ABSENCE_BY_USER_AND_DATE_BETWEEN_DATE_TO_AND_DATE_FROM, query = "select a from Absence a where a.user = :user and :date between a.dateFrom and a.dateTo"),
		@NamedQuery(name = Queries.GET_ALL_ABSENCES_BY_USER, query = "select a from Absence a where a.user = :user"),
})
public class Absence {

	private long id;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private AbsenceReason reason;
	private AbsenceStatus status;
	private String details;
	private User user;

	public Absence() {}

	public Absence(LocalDate dateFrom, LocalDate dateTo, AbsenceReason reason, AbsenceStatus status, String details, User user) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.reason = reason;
		this.status = status;
		this.details = details;
		this.user = user;
	}

	public static class Transfer {
		private String dateFrom;
		private String dateTo;
		private String reason;
		private String status;
		private String details;
		private String username;
		private String id;

		public Transfer(Absence a) {
			dateFrom = a.getDateFrom().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			dateTo = a.getDateTo().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			reason = a.getReason().toString();
			status = a.getStatus().toString();
			details = a.getDetails();
			username = a.getUser().getName();
			id = String.valueOf(a.getId());
		}

		public String getDateFrom() {
			return dateFrom;
		}

		public void setDateFrom(String dateFrom) {
			this.dateFrom = dateFrom;
		}

		public String getDateTo() {
			return dateTo;
		}

		public void setDateTo(String dateTo) {
			this.dateTo = dateTo;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public String getDetails() {
			return details;
		}

		public void setDetails(String details) {
			this.details = details;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
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
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	@JsonFormat(pattern="yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public LocalDate getDateTo() {
		return dateTo;
	}

	public void setDateTo(LocalDate dateTo) {
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

	public AbsenceStatus getStatus() {
		return status;
	}

	public void setStatus(AbsenceStatus status) {
		this.status = status;
	}

	public static List<Transfer> asTransferObjects(Collection<Absence> absences) {
		return absences.stream()
				.map(Transfer::new)
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Absence{" +
				"id=" + id +
				", dateFrom=" + dateFrom +
				", dateTo=" + dateTo +
				", reason=" + reason +
				", status=" + status +
				", details='" + details + '\'' +
				", user=" + user +
				'}';
	}
}
