package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.PassengerState;
import lombok.Data;

@Data
public class ClientPassenger {
    private ClientChild childDetails;
    private ClientPediStop stopReserved;
    private ClientPediStop stopTaken;
    private ClientPediStop stopDelivered;
    private boolean isReserved;
    private PassengerState state;

    public ClientPassenger(ClientChild childDetails, ClientPediStop stopReserved, ClientPediStop stopTaken, ClientPediStop stopDelivered, boolean isReserved, PassengerState state) {
        this.childDetails = childDetails;
        this.stopReserved = stopReserved;
        this.stopTaken = stopTaken;
        this.stopDelivered = stopDelivered;
        this.isReserved = isReserved;
        this.state = state;
    }

    public ClientPassenger() {

    }
}
