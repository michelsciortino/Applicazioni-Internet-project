package it.polito.ai.labs.lab3.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class User {

    @Id
    private String id;

    @NotNull
    @Indexed(unique = true)
    private String username;

    @DBRef
    Credential credential;
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @Nullable
    private List<String> contacts;
    @Nullable
    private List<Child> children;
    @Nullable
    private List<String> lines;

}
