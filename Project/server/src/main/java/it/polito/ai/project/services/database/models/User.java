package it.polito.ai.project.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class User {

    @Id
    private String id;

    @Email
    @NotNull
    @Indexed(unique = true)
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
}
