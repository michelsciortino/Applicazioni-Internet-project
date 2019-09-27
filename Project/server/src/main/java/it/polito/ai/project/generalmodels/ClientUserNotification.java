package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.NotificationsType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.Date;
@Data
public class ClientUserNotification {

    private String id;
    @Email
    private String performerUsername;
    @Email
    @Nullable
    private String targetUsername;
    @NotNull
    private NotificationsType type;
    @NotNull
    private Date date;
    private boolean broadcast;
    @Nullable
    private ClientRace broadcastRace;
    @Nullable
    private Object parameters;
    @Size(min = 2, max = 140)
    private String message;
    @NotNull
    private Boolean isRead;

    public ClientUserNotification(String id, @Email String performerUsername,@Email String targetUsername, @NotNull NotificationsType type, @NotNull Date date, boolean broadcast, ClientRace broadcastRace, @Nullable Object parameters, @Size(min = 2, max = 140) String message, @NotNull Boolean isRead) {
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
}
