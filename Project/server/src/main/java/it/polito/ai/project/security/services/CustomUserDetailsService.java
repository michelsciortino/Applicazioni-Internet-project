package it.polito.ai.project.security.services;

import it.polito.ai.project.services.database.DatabaseServiceInterface;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final DatabaseServiceInterface database;

    public CustomUserDetailsService(DatabaseServiceInterface database) {
        this.database = database;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return database.getCredentials(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Username: " + username + " not found");
        }
        //EmbeddedLdapProperties.Credential user = database.getCredentials(username).orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
    }
}