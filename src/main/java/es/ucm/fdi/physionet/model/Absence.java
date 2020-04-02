/*
* Representa una ausencia de un usuario. Esto se usará para los cuadrantes de horarios entre los médicos.
*/

package es.ucm.fdi.physionet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.ucm.fdi.physionet.model.enums.AbsenceReason;
import es.ucm.fdi.physionet.model.util.Queries;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@NamedQueries({
		@NamedQuery(name = Queries.GET_ALL_ABSENCES, query = "select a from Absence a")
})
public class Absence {

	private long id;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private AbsenceReason reason;
	private String details;
	private User user;

	public Absence() {}

	public Absence(LocalDate dateFrom, LocalDate dateTo, AbsenceReason reason, String details, User user) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.reason = reason;
		this.details = details;
		this.user = user;
	}

	public static class Transfer {
		private String dateFrom;
		private String dateTo;
		private String reason;
		private String details;
		private String username;
		private String id;

		public Transfer(Absence a) {
			dateFrom = a.getDateFrom().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			dateTo = a.getDateTo().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			reason = a.getReason().toString();
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

	public static List<Transfer> asTransferObjects(Collection<Absence> absences) {
		ArrayList<Transfer> objects = new ArrayList<>();

		for(Absence a : absences) {
			objects.add(new Transfer(a));
		}

		return objects;
	}

	@Override
	public String toString() {
		return "Absence{" +
				"id=" + id +
				", dateFrom=" + dateFrom +
				", dateTo=" + dateTo +
				", reason=" + reason +
				", details='" + details + '\'' +
				'}';
	}
}
