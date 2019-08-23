package it.polito.ai.project.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Document(collection = "users_notifications")
@Data
public class UserNotification {
    //TODO: da fare con estensioni se si riesce....
    @Email
    @Id
    private String username;
    @NotNull
    private NotificationsType type;
    @NotNull
    private Date date;
    @Nullable
    private Object parameters;
    @Size(min = 2, max = 140)
    private String message;
    @NotNull
    private Boolean isRead;

    public UserNotification(@Email String username, @NotNull NotificationsType type, @NotNull Date date, @Nullable Object parameters, @Size(min = 2, max = 140) String message, @NotNull Boolean isRead) {
        this.username = username;
        this.type = type;
        this.date = date;
        this.parameters = parameters;
        this.message = message;
        this.isRead = isRead;
    }
}