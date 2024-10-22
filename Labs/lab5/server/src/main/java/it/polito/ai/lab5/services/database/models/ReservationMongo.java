package it.polito.ai.lab5.services.database.models;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reservations")
@Data
@Builder
@CompoundIndex(def = "{'childName':1, 'direction':1, 'data':1}", unique = true, name = "reservationIndex")
public class ReservationMongo {
    @Id
    private ObjectId id;

    private String userID;
    private String childName;
    private String childSurname;

    private String childCf;
    private String lineName;
    private String stopName;
    private String direction;
    private Date data;
    private boolean present;
}


