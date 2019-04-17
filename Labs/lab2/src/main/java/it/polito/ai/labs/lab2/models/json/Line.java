package it.polito.ai.labs.lab2.models.json;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@JsonComponent
//@Builder
public class Line {
    public String name;
    public ArrayList<PediStop> outboundStops;
    public ArrayList<PediStop> returnStops;

    public Line(){}

    public Line(String name, ArrayList<PediStop> outboundStops, ArrayList<PediStop> returnStops) {
        this.name = name;
        this.outboundStops = outboundStops;
        this.returnStops = returnStops;
    }
}

