package it.polito.ai.lab5.services.database.repositories;

import it.polito.ai.lab5.services.database.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

    @Query(value = "{}", fields = "{'credential' : 0}")
    Page<User> findAllNoCredential(Pageable pegeable);

    User findByUsername(String username);
}
