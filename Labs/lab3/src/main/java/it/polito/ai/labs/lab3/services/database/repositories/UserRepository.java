package it.polito.ai.labs.lab3.services.database.repositories;

import it.polito.ai.labs.lab3.services.database.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}