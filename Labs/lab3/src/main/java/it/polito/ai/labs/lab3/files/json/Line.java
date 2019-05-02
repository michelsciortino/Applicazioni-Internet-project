package it.polito.ai.labs.lab3.files.json;

import org.springframework.boot.jackson.JsonComponent;

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

