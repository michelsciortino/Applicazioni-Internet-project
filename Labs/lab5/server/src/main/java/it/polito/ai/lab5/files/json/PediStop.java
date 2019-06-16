package it.polito.ai.lab5.files.json;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
//@Builder
public class PediStop {
    public String name;
    public float longitude;
    public float latitude;
    public String time;

    public PediStop() {
    }

    public PediStop(String name, float longitude, float latitude, String time) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }
}
