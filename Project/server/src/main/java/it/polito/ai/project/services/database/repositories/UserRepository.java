package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByNameContains(String filter, Pageable pageable);

    Page<User> findAllBySurnameContains(String filter, Pageable pageable);

    Page<User> findAllByUsernameContains(String filter, Pageable pageable);
}
