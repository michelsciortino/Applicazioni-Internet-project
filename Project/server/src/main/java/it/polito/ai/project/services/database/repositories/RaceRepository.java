package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.Companion;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.Race;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RaceRepository extends MongoRepository<Race, String> {
    List<Race> findAllByDate(Date date);

    Optional<Race> findRaceByDateAndLineNameAndDirection(Date date, String lineName, DirectionType direction);

    @Query("{'companion' : ?0, 'date':{$gt:?1}}")
    List<Race> findAllByCompanions(Companion companion, Date date);

    @Query("{'companion' : ?0, 'date':{$eq:?1}}")
    List<Race> findAllByCompanionsAndEqDate(Companion companion, Date date);
}
