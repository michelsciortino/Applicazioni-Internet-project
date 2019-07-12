package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Page<User> findAll(Pageable pageable);
}
