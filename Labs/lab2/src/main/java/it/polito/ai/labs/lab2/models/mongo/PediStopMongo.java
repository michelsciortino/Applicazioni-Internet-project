package it.polito.ai.labs.lab2.models.mongo;

import lombok.Data;

@Data
public class PediStopMongo {

    private String name;
    private float longitude;
    private float latitude;

    public PediStopMongo(){ }

    public PediStopMongo(float longitude, float latitude, String name) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

}

