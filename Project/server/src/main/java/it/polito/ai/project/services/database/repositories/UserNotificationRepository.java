package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.Race;
import it.polito.ai.project.services.database.models.UserNotification;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {

    Page<UserNotification> findAllByTargetUsername(String TargetUsername, Pageable pageable);

    Optional<UserNotification> findById(String id);

    @Query("{'date' :{$eq : ?2}}")
    Optional<List<UserNotification>> findAllByPerformerUsernameAndTargetUsernameAndEqDate(String performerUsername, String targetUsername, Date date);
}
