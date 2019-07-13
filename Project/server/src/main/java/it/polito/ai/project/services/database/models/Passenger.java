package it.polito.ai.project.services.database.models;

import lombok.Data;

@Data
public class Passenger {
    private Child childDetails;
    private boolean isReserved;
    private boolean isPresent;

    public Passenger(Child childDetails, boolean isReserved, boolean isPresent) {
        this.childDetails = childDetails;
        this.isReserved = isReserved;
        this.isPresent = isPresent;
    }
}
