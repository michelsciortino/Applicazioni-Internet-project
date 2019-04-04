package it.polito.ai.labs.lab2.lab2.models;

import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;

@JsonComponent
public class Line {
    public long id;
    public String name;
    ArrayList<PediStop> stops;

    public Line() {
        stops = new ArrayList<PediStop>();
    }
}

