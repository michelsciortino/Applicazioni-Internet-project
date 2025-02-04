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

@Document(collection = "credentials")
@Data
public class Credential implements UserDetails {

    @Id
    private String id;
    @NotNull
    private String password;
    @NotNull
    @Indexed(unique = true)
    private String username;

    boolean enable=true;
    boolean credentialsNotExpired=true;
    boolean accountNotLocked=true;
    boolean accountNotExpired=true;

    private List<String> roles=new ArrayList<>();

    public Credential(String password, String username, List<String> roles) {
        this.password = password;
        this.username = username;
        this.roles = roles;
    }

    public Credential() {
    }

    public Credential(String id, @NotNull String password, @NotNull String username, boolean enable, boolean credentialsExpired, boolean accountLocked, boolean accountExpired, List<String> roles) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.enable = enable;
        this.credentialsNotExpired = credentialsExpired;
        this.accountNotLocked = accountLocked;
        this.accountNotExpired = accountExpired;
        this.roles = roles;
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

