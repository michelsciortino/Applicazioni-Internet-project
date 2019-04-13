package it.polito.ai.labs.lab2.models.rest;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Reservation {
    private String childName;
    private String stopName;
    private String direction;
}
