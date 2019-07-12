package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.Child;
import it.polito.ai.project.services.database.models.UserNotification;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ClientUser {

    @Email
    @NotNull
    private String username;

    @Size(min = 2, max = 30)
    @NotNull
    private String name;
    @Size(min = 2, max = 30)
    @NotNull
    private String surname;

    @Nullable
    private List<String> contacts;
    @Nullable
    private List<Child> children;
    @Nullable
    private List<String> lines;

    @Nullable
    private List<UserNotification> notifications;

    public ClientUser(@Email @NotNull String username, @Size(min = 2, max = 30) @NotNull String name, @Size(min = 2, max = 30) @NotNull String surname, @Nullable List<String> contacts, @Nullable List<Child> children, @Nullable List<String> lines, @Nullable List<UserNotification> notifications) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.contacts = contacts;
        this.children = children;
        this.lines = lines;
        this.notifications = notifications;
    }
}
