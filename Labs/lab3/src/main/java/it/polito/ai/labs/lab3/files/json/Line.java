package it.polito.ai.labs.lab3.files.json;

import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@JsonComponent
//@Builder
public class Line {
    public String name;
    public ArrayList<PediStop> outboundStops;
    public ArrayList<PediStop> returnStops;
    public List<LocalTime> startTimes;

    public Line(){}

    public Line(String name, ArrayList<PediStop> outboundStops, ArrayList<PediStop> returnStops) {
        this.name = name;
        this.outboundStops = outboundStops;
        this.returnStops = returnStops;
    }
}

