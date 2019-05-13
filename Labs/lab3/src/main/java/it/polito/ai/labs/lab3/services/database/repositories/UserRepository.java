package it.polito.ai.labs.lab3.services.database.repositories;

import it.polito.ai.labs.lab3.services.database.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {
}
