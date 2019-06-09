package it.polito.ai.lab5.services.database.models;

import lombok.Data;

@Data
public class PediStopMongo {

    private String name;
    private float longitude;
    private float latitude;

    public PediStopMongo() {
    }

    public PediStopMongo(float longitude, float latitude, String name) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

}

