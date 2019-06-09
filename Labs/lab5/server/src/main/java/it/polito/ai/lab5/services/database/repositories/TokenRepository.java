package it.polito.ai.lab5.services.database.repositories;

import it.polito.ai.lab5.services.database.models.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByConfirmationToken(String confirmationToken);
}
