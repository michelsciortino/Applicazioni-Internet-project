package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.Companion;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.Race;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RaceRepository extends MongoRepository<Race, String> {
    List<Race> findAllByData(Data date);
    Optional<Race> findRaceByDataAndLineNameAndDirection(Date date, String lineName, String direction);
    @Query("{'companion' : ?0, 'date':{$gt:?1}}")
    List<Race> findAllByCompanions(Companion companion, Date date);
}
