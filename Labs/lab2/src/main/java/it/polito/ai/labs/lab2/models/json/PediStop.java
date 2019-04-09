package it.polito.ai.labs.lab2.models.json;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class PediStop {
    public float longitude;
    public float latitude;
    public String name;
}
