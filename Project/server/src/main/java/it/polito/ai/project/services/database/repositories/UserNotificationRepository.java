package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {
    Page<UserNotification> findAllByUsername(Pageable pageable, String username);
}