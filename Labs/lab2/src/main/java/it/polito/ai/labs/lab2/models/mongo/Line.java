package it.polito.ai.labs.lab2.models.mongo;

import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection="lines")
public class Line {
    //@Id
    //public ObjectId id;
    public String name;
    ArrayList<PediStop> outboundStops;
    ArrayList<PediStop> returnStops;

    public Line() {
        outboundStops = new ArrayList<>();
        returnStops = new ArrayList<>();
    }

    public Line(String name, ArrayList<PediStop> outboundStops,ArrayList<PediStop> returnStops) {
        this.name = name;
        this.outboundStops = outboundStops;
        this.returnStops = returnStops;
    }
}

