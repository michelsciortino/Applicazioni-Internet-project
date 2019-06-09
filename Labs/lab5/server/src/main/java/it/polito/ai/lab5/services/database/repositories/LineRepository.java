package it.polito.ai.lab5.services.database.repositories;

import it.polito.ai.lab5.services.database.models.LineMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LineRepository extends MongoRepository<LineMongo, String> {
    @Query(value = "{}", fields = "{'name' : 1, '_id' : 0}")
    List<LineMongo> findAllName();

    @Query("{ 'name' : ?0 }")
    LineMongo findLineByName(String name);


}
