package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByToken(String token);
}
