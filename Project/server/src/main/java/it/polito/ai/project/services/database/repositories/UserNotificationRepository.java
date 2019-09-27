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
    @Query("{'user_notifications.broadcastRace.companions.username' : ?0, 'user_notifications.broadcastRace.passengers.childDetails.parentId' : ?1}")
    Page<UserNotification> findAllByBroadcastIsTrueAndBroadcastRace_CompanionsContainsAndBroadcastRace_PassengersContains( String companion, String parent, Pageable pageable);
    @Query("{'user_notifications.broadcastRace.lineName' :{$in: ?0}}")
    Page<UserNotification> findAllByBroadcastIsTrueAndBroadcastRace_LineNameIn(List<String> lineName, Pageable pageable);
    Optional<UserNotification> findById(String id);
    Optional<UserNotification>findByPerformerUsernameAndTargetUsernameAndDateEq(String performerUsername, String targetUsername, Date date);
    Optional<UserNotification>findByBroadcastIsTrueAndPerformerUsernameAndRaceAndDateEq(String performerUsername, Race race, Date date);
    @Override
    <S extends UserNotification> Optional<S> findOne(Example<S> example);
}
