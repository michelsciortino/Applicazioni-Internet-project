package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.UserCredentials;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends MongoRepository<UserCredentials, String> {
    Optional<UserCredentials> findByUsername(String username);

    Page<UserCredentials> findAllByRolesContains(String role, Pageable pageable);
}
