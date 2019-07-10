package it.polito.ai.project.services.database.repositories;

import it.polito.ai.project.services.database.models.Line;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends MongoRepository<Line, String> {

    Optional<Line> findLineByName(String name);

    @Query(value = "{}", fields = "{'name' : 1, '_id' : 0}")
    List<Line> findAllName();
}
