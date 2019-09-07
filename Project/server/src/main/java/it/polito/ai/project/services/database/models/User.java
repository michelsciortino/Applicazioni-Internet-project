package it.polito.ai.project.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

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

    public User(@Email @NotNull String username, @Size(min = 2, max = 30) @NotNull String name, @Size(min = 2, max = 30) @NotNull String surname, @Nullable List<String> contacts, @Nullable List<Child> children, @Nullable List<String> lines) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.contacts = contacts;
        this.children = children;
        this.lines = lines;
    }
}
