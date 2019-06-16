package it.polito.ai.lab5.services.database.models;

import lombok.Data;

@Data
public class PediStopMongo {

    private String name;
    private float longitude;
    private float latitude;
    private String time;

    public PediStopMongo() {
    }

    public PediStopMongo(float longitude, float latitude, String name, String time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.time = time;
    }

}

