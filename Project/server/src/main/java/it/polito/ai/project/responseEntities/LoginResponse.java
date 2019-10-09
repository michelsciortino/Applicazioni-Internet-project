package it.polito.ai.project.responseEntities;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class LoginResponse {
    @NotNull
    private String mail;
    @NotNull
    private String token;
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private List<String> roles;

    public LoginResponse(@NotNull String mail, @NotNull String token, @NotNull String name, @NotNull String surname, @NotNull List<String> roles) {
        this.mail = mail;
        this.token = token;
        this.name = name;
        this.surname = surname;
        this.roles = roles;
    }
}
