package it.polito.ai.lab5.services.database.repositories;

import it.polito.ai.lab5.services.database.models.Credential;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository extends MongoRepository<Credential, String> {
    Optional<Credential> findByUsername(String username);

    Optional<Credential> findById(String id);

    @Query(value = "{}", fields = "{'username' : 1, '_id' : 0}")
    List<Credential> findAllUsername();

    @Query(value = "{}", fields = "{'username' : 1, '_id' : 0}")
    Page<Credential> findAllUsername(Pageable pegeable);
}