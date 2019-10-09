package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.NotificationsType;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.Date;
@Data
public class ClientUserNotification {
    @Nullable
    private String id;
    @NotNull
    @Email
    private String performerUsername;
    @Email
    @Nullable
    private String targetUsername;
    @NotNull
    private NotificationsType type;
    @NotNull
    private Date date;
    @Nullable
    private ClientRace referredRace;
    @Size(min = 2, max = 140)
    private String message;
    @Nullable
    private Boolean isRead;

    public ClientUserNotification(String id, @Email String performerUsername,@Email String targetUsername, @NotNull NotificationsType type, @NotNull Date date, ClientRace race, @Size(min = 2, max = 140) String message, @NotNull Boolean isRead) {
        this.id = id;
        this.performerUsername = performerUsername;
        this.targetUsername = targetUsername;
        this.type = type;
        this.date = date;
        this.referredRace = race;
        this.message = message;
        this.isRead = isRead;
    }
}
