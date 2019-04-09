package it.polito.ai.labs.lab2.lab2.models;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class PediStop {
    public float longitude;
    public float latitude;
    public String name;
}
