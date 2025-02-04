package it.polito.ai.labs.lab3.security.services;

import it.polito.ai.labs.lab3.services.database.models.Credential;
import it.polito.ai.labs.lab3.services.database.repositories.CredentialRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private CredentialRepository users;

    public CustomUserDetailsService(CredentialRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credential a = this.users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
        return a;
    }
}