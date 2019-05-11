package it.polito.ai.labs.lab3.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Document(collection = "user")
@Data
public class User implements UserDetails {

    @Id
    private String id;
    @NotNull
    private String password;
    @NotNull
    @Indexed(unique = true)
    private String username;

    boolean enable=true;
    boolean credentialsExpired=true;
    boolean accountLocked=true;
    boolean accountExpired=true;

    private List<String> roles=new ArrayList<>();

    public User(String password, String username, List<String> roles) {
        this.password = password;
        this.username = username;
        this.roles = roles;
    }

    public User() {
    }

    public User(String id, @NotNull String password, @NotNull String username, boolean enable, boolean credentialsExpired, boolean accountLocked, boolean accountExpired, List<String> roles) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.enable = enable;
        this.credentialsExpired = credentialsExpired;
        this.accountLocked = accountLocked;
        this.accountExpired = accountExpired;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}

