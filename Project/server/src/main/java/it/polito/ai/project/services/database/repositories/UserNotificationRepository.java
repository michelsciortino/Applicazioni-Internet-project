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
    @Query("{'broadcastRace.companions.username' : ?0, 'broadcastRace.passengers.childDetails.parentId' : ?1}")
    Page<UserNotification> findAllByBroadcastIsTrueAndBroadcastRace_CompanionsContainsAndBroadcastRace_PassengersContains( String companion, String parent, Pageable pageable);
    @Query("{'broadcastRace.lineName' :{$in: ?0}}")
    Page<UserNotification> findAllByBroadcastIsTrueAndBroadcastRace_LineNameIn(List<String> lineName, Pageable pageable);
    Optional<UserNotification> findById(String id);
    @Query("{'date' :{$eq : ?2}}")
    Optional<UserNotification> findByPerformerUsernameAndTargetUsernameAndEqDate(String performerUsername, String targetUsername, Date date);
    @Query("{'date' :{$eq : ?2}}")
    Optional<UserNotification> findByBroadcastIsTrueAndPerformerUsernameAndBroadcastRaceAndEqDate(String performerUsername, Race broadcastRace, Date date);
    @Override
    <S extends UserNotification> Optional<S> findOne(Example<S> example);
}
