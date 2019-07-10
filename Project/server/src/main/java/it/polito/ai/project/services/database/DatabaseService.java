package it.polito.ai.project.services.database;

import it.polito.ai.project.services.database.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DatabaseService implements DatabaseServiceInterface {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private RaceRepository raceRepository;
    @Autowired
    private UserCredentialsRepository userCredentialsRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
}
