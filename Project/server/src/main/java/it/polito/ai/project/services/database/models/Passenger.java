package it.polito.ai.project.services.database.models;

import lombok.Data;

@Data
public class Passenger {
    private Child childDetails;
    private PediStop stopReserved;
    private PediStop stopTaken;
    private PediStop stopDelivered;
    private boolean isReserved;
    private PassengerState state;

    public Passenger(Child childDetails, PediStop stopReserved, PediStop stopTaken, PediStop stopDelivered, boolean isReserved, PassengerState state) {
        this.childDetails = childDetails;
        this.stopReserved = stopReserved;
        this.stopTaken = stopTaken;
        this.stopDelivered = stopDelivered;
        this.isReserved = isReserved;
        this.state = state;
    }
}
