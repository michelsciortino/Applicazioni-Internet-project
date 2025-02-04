package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RaceRepository extends MongoRepository<Race, String> {
    List<Race> findAllByDate(Date date);

    Optional<Race> findRaceByDateAndLineNameAndDirection(Date date, String lineName, DirectionType direction);

    List<Race> findAllByLineName( String lineName);

    List<Race> findAllByLineNameAndDate( String lineName, Date date);

    //@Query("{'date' : {$gt : ?1, $lt : ?2}}")
    //List<Race> findAllByLineNameAndDateBetween(String lineName, Date fromDate, Date toDate);
    List<Race> findAllByLineNameAndDateBetween(String lineName, Date fromDate, Date toDate);

    List<Race> findAllByAndDateBetween(Date fromDate, Date toDate);

    List<Race> findAllByLineNameAndDirection( String lineName, DirectionType direction);

    List<Race> findAllByLineNameAndDirectionAndDate( String lineName, DirectionType direction, Date date);

    //@Query("{'date' : {$gt : ?1, $lt : ?2}}")
    //List<Race> findAllByLineNameAndDirectionAndDateBetween(String lineName, DirectionType direction, Date fromDate, Date toDate );
    List<Race> findAllByLineNameAndDirectionAndDateBetween(String lineName, DirectionType direction, Date fromDate, Date toDate);

    @Query("{'companions.userDetails.username' : ?0, 'date':{$gt:?1}}")
    List<Race> findAllByCompanionsAndDateGreaterThan(String companion, Date date);
    //List<Race> findAllByCompanionsContainsAndDateAfter(String companion, Date date);

    @Query("{'companions.userDetails.username' : ?0, 'date':{$eq:?1}}")
    List<Race> findAllByCompanionsAndEqDate(String companion, Date date);
    //List<Race> findAllByCompanionsContainsAndDateEquals(String companion, Date date);


    //List<Race> findAllByPassengersContainsAndDateEquals(String parentId, Date date);

    @Query("{'passengers.childDetails.parentId' : ?0, 'date':{$eq:?1}}")
    List<Race> findAllByPassengersAndEqDate(String companion, Date date);

    List<Race> findAllByRaceStateIsNot(RaceState state);

    List<Race> findAllByRaceStateIs(RaceState state);

    List<Race> findAllByLineNameAndRaceStateIsNot(String lineName, RaceState state);

    List<Race> findAllByLineNameAndRaceStateIs(String lineName, RaceState state);
}
