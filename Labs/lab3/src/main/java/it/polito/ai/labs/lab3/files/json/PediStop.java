package it.polito.ai.labs.lab3.files.json;

import org.springframework.boot.jackson.JsonComponent;

import java.time.Duration;


@JsonComponent
//@Builder
public class PediStop {
    public String name;
    public float longitude;
    public float latitude;
    public Duration offset;

    public PediStop(){}

    public PediStop( String name, float longitude, float latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
