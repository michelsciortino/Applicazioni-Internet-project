package it.polito.ai.project.generalmodels;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClientUser {

    @Email
    @NotNull
    private String mail;

    @Size(min = 2, max = 30)
    @NotNull
    private String name;
    @Size(min = 2, max = 30)
    @NotNull
    private String surname;

    @Nullable
    private List<String> contacts;
    @Nullable
    private List<ClientChild> children;
    @Nullable
    private List<String> lines;
    @NotNull
    private List<String> roles;

    public ClientUser(@Email @NotNull String mail, @Size(min = 2, max = 30) @NotNull String name, @Size(min = 2, max = 30) @NotNull String surname, @Nullable List<String> contacts, @Nullable List<ClientChild> children, @Nullable List<String> lines, @NotNull
    @NotNull List<String> roles) {
        this.mail = mail;
        this.name = name;
        this.surname = surname;
        this.contacts = contacts;
        this.children = children;
        this.lines = lines;
        this.roles = roles;
    }

    public ClientUser() {
        contacts = new ArrayList<>();
        children = new ArrayList<>();
        lines = new ArrayList<>();
    }
}
