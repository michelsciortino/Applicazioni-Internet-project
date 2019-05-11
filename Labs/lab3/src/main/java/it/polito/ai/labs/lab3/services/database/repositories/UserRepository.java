package it.polito.ai.labs.lab3.services.database.repositories;

import it.polito.ai.labs.lab3.services.database.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(String id);

    @Query(value = "{}", fields = "{'username' : 1, '_id' : 0}")
    List<User> findAllUsername();
}