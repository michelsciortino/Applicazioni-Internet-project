package it.polito.ai.labs.lab2.repositories;

import it.polito.ai.labs.lab2.models.mongo.Line;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LineRepository extends MongoRepository<Line,String> {
    @Query(value = "{}", fields = "{'name' : 1, '_id' : 0}")
    List<Line> findAllNameAndId();
}
