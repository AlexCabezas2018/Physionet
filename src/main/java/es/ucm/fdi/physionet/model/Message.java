/*
* La entidad "message" representa al mensaje que escribe un usuario a otro en la aplicación. Se utilizará para las conver-
* saciones entre los usuarios.
*/

package es.ucm.fdi.physionet.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import es.ucm.fdi.physionet.model.util.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A message that users can send each other.
 *
 * @author mfreire
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Queries.GET_MESSAGES_CONVERSATION,
                query = "SELECT m FROM Message m where m.sender = :userFrom AND m.recipient.username = :userTo " +
                        "OR m.sender.username = :userTo AND m.recipient = :userFrom ORDER BY m.dateSent ASC")
})
public class Message {

    private static Logger log = LogManager.getLogger(Message.class);

    private long id;
    private User sender;
    private User recipient;
    private String text;

    private LocalDateTime dateSent;
    private LocalDateTime dateRead;

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
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @ManyToOne(targetEntity = User.class)
    @JsonBackReference
    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDateTime getDateRead() {
        return dateRead;
    }

    public void setDateRead(LocalDateTime dateRead) {
        this.dateRead = dateRead;
    }
}