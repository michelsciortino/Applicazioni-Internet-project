package it.polito.ai.lab5.controllers.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Reservation {
    private String childName;
    private String stopName;
    private String direction;
}
