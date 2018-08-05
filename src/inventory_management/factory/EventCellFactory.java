package inventory_management.factory;

import javafx.scene.image.Image;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by Kh3m on 7/22/2018
 */
public class EventCellFactory {

    private int eventId;
    private Image userImage;
    private String userName;
    private String userRole;
    private String description;
    private Timestamp dateTime;

    public EventCellFactory(int eventId, Image userImage, String userName, String userRole,
                            String description, Timestamp dateTime)
    {
        this.userImage = userImage;
        this.userName = userName;
        this.userRole = userRole;
        this.description = description;
        this.dateTime = dateTime;
        this.eventId = eventId;
    }

    public int getEventId () { return eventId; }

    public Image getUserImage() {
        return userImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }
}
