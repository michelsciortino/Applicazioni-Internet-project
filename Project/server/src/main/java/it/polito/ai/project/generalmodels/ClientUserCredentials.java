package it.polito.ai.project.generalmodels;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class ClientUserCredentials{
    boolean enable;
    boolean credentialsNotExpired;
    boolean accountNotLocked;
    boolean accountNotExpired;
    @NotNull
    @Email
    private String username;
    @NotNull
    private List<String> roles;

    public ClientUserCredentials(@NotNull @Email String username, @NotNull List<String> roles, boolean enable, boolean credentialsNotExpired, boolean accountNotLocked, boolean accountNotExpired) {
        this.username = username;
        this.roles = roles;
        this.enable = enable;
        this.credentialsNotExpired = credentialsNotExpired;
        this.accountNotLocked = accountNotLocked;
        this.accountNotExpired = accountNotExpired;
    }
}
