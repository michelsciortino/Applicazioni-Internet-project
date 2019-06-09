package it.polito.ai.lab5.services.database.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "lines")
@Data
public class LineMongo {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String name;
    private ArrayList<PediStopMongo> outboundStops;
    private ArrayList<PediStopMongo> returnStops;

    public LineMongo() {
        outboundStops = new ArrayList<>();
        returnStops = new ArrayList<>();
    }

    public LineMongo(String name, ArrayList<PediStopMongo> outboundStops, ArrayList<PediStopMongo> returnStops) {
        this.name = name;
        this.outboundStops = outboundStops;
        this.returnStops = returnStops;
    }
}

