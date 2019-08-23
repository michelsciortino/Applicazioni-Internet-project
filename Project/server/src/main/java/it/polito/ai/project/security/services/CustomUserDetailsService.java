package it.polito.ai.project.security.services;

import it.polito.ai.project.services.database.AuthServiceInterface;
import it.polito.ai.project.services.database.DatabaseServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthServiceInterface authService;

    @Autowired
    public CustomUserDetailsService(AuthServiceInterface authService) {
        this.authService = authService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return authService.getCredentials(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Username: " + username + " not found");
        }
        //EmbeddedLdapProperties.Credential user = database.getCredentials(username).orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
    }
}