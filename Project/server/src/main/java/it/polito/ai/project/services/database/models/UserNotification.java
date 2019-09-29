package it.polito.ai.project.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Document(collection = "users_notifications")
@Data
public class UserNotification {
    @Id
    private String id;
    @Email
    private String performerUsername;
    @Email
    @Nullable
    private String targetUsername;
    @NotNull
    private NotificationsType type;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;
    private boolean broadcast;
    @Nullable
    @Reference
    private Race broadcastRace;
    @Nullable
    private Object parameters;
    @Size(min = 2, max = 140)
    private String message;
    @NotNull
    private Boolean isRead;

    public UserNotification(){
    }

    public UserNotification(String id, @Email String performerUsername,@Email String targetUsername, @NotNull NotificationsType type, @NotNull Date date, boolean broadcast, Race broadcastRace, @Nullable Object parameters, @Size(min = 2, max = 140) String message, @NotNull Boolean isRead) {
        this.id = id;
        this.performerUsername = performerUsername;
        this.targetUsername = targetUsername;
        this.type = type;
        this.date = date;
        this.broadcast = broadcast;
        this.broadcastRace = broadcastRace;
        this.parameters = parameters;
        this.message = message;
        this.isRead = isRead;
    }
    public UserNotification(@Email String performerUsername,@Email String targetUsername, @NotNull NotificationsType type, @NotNull Date date, boolean broadcast, Race broadcastRace, @Nullable Object parameters, @Size(min = 2, max = 140) String message, @NotNull Boolean isRead) {
        this.performerUsername = performerUsername;
        this.targetUsername = targetUsername;
        this.type = type;
        this.date = date;
        this.broadcast = broadcast;
        this.broadcastRace = broadcastRace;
        this.parameters = parameters;
        this.message = message;
        this.isRead = isRead;
    }
}
