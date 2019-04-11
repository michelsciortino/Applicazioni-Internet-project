package it.polito.ai.labs.lab2.models.mongo;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reservations")
@Data
@CompoundIndex(name = "reservationID",unique = true,def = "{'userID':1,'childName':1,'data':1")
@Builder
public class ReservationMongo {
    @Id
    private ObjectId id;
    private String userID;
    private String childName;
    private String lineName;
    private String stopName;
    private String direction;
    private LocalDateTime data;
}


