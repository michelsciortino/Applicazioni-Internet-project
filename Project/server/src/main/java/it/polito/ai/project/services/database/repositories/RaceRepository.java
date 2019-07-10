package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.Race;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.xml.crypto.Data;
import java.util.List;

public interface RaceRepository extends MongoRepository<Race, String> {
    List<Race> findAllByData(Data date);
}
