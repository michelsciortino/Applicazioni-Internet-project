package it.polito.ai.project.generalmodels;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClientUserCredentials {
    @NotNull
    @Email
    private String username;

    @NotNull
    private List<String> roles = new ArrayList<>();
    boolean enable = true;
    boolean credentialsNotExpired = true;
    boolean accountNotLocked = true;
    boolean accountNotExpired = true;

    public ClientUserCredentials(@NotNull @Email String username, @NotNull List<String> roles, boolean enable, boolean credentialsNotExpired, boolean accountNotLocked, boolean accountNotExpired) {
        this.username = username;
        this.roles = roles;
        this.enable = enable;
        this.credentialsNotExpired = credentialsNotExpired;
        this.accountNotLocked = accountNotLocked;
        this.accountNotExpired = accountNotExpired;
    }
}
