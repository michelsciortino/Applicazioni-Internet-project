package it.polito.ai.labs.lab2.models.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="stops")
public class PediStop {
    public float longitude;
    public float latitude;
    public String name;

    public PediStop(){ }

    public PediStop(float longitude, float latitude, String name) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

}

