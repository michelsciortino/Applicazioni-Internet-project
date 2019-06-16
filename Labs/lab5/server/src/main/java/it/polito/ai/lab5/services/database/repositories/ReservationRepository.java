package it.polito.ai.lab5.services.database.repositories;

import it.polito.ai.lab5.services.database.models.ReservationMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ReservationRepository extends MongoRepository<ReservationMongo, String> {

    @Query("{'stopName' : ?0,'direction':?1,'data':{$eq:?2},'lineName' : ?3}")
    List<ReservationMongo> getReservationByStopNameAndDirection(String stopName, String direction, LocalDate data, String lineName);

    List<ReservationMongo> getReservationMongoByStopNameAndDirectionAndData(String stopName, String direction, Date data, String lineName);
}
