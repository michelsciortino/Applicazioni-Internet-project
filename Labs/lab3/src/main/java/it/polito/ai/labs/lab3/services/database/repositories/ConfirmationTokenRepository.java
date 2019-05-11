package it.polito.ai.labs.lab3.services.database.repositories;

import it.polito.ai.labs.lab3.services.database.models.ConfirmationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, String> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
}
