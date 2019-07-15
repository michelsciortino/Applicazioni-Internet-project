package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.Child;
import it.polito.ai.project.services.database.models.PassengerState;
import it.polito.ai.project.services.database.models.PediStop;
import lombok.Data;

@Data
public class ClientPassenger {
    private Child childDetails;
    private PediStop stopReserved;
    private PediStop stopTaken;
    private PediStop stopDelivered;
    private boolean isReserved;
    private PassengerState state;

    public ClientPassenger(Child childDetails, PediStop stopReserved, PediStop stopTaken, PediStop stopDelivered, boolean isReserved, PassengerState state) {
        this.childDetails = childDetails;
        this.stopReserved = stopReserved;
        this.stopTaken = stopTaken;
        this.stopDelivered = stopDelivered;
        this.isReserved = isReserved;
        this.state = state;
    }
}
