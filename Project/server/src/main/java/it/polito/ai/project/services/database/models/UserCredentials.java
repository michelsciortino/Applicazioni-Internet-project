package it.polito.ai.project.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Document(collection = "users_credentials")
@Data
public class UserCredentials implements UserDetails {

    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    @Email
    private String username;
    @NotNull
    private String password;
    @NotNull
    private List<String> roles = new ArrayList<>();

    boolean enable = false;
    boolean credentialsNotExpired = true;
    boolean accountNotLocked = true;
    boolean accountNotExpired = true;

    public UserCredentials() {
    }

    public UserCredentials(@NotNull @Email String username, @NotNull String password, @NotNull List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public UserCredentials(String id, @NotNull String password, @NotNull @Email String username, boolean enable, boolean credentialsExpired, boolean accountLocked, boolean accountExpired, List<String> roles) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.enable = enable;
        this.credentialsNotExpired = credentialsExpired;
        this.accountNotLocked = accountLocked;
        this.accountNotExpired = accountExpired;
        this.roles = roles;
    }

    public UserCredentials(@NotNull @Email String username, @NotNull String password, @NotNull List<String> roles, boolean enable) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.enable = enable;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNotExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNotLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNotExpired;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
