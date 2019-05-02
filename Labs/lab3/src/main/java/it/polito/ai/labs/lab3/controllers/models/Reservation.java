package it.polito.ai.labs.lab3.controllers.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Reservation {
    private String childName;
    private String stopName;
    private String direction;
}
