/*
* La entidad "message" representa al mensaje que escribe un usuario a otro en la aplicación. Se utilizará para las conver-
* saciones entre los usuarios.
*/

package es.ucm.fdi.physionet.model;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class Message {

    private long id;

    private User userFrom;
    private User userTo;

    private ZonedDateTime timestamp;
    private String content;
    private Boolean seen;

    public Message() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    @ManyToOne(targetEntity = User.class)
    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean isSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
