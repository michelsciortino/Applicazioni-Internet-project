package it.polito.ai.project.services.database.models;

import lombok.Data;

@Data
public class Passenger {
    private Child childDetails;
    private boolean isReserved;
    private boolean isPresent;
}
