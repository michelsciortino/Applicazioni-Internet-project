package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.PassengerState;
import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
@Data
public class JsonPassenger {
    private JsonChild childDetails;
    private ClientPediStop stopReserved;
    private ClientPediStop stopTaken;
    private ClientPediStop stopDelivered;
    private boolean reserved;
    private PassengerState state;

    public JsonPassenger() {
        reserved = false;
        state = PassengerState.ABSENT;
    }

    public JsonPassenger(JsonChild childDetails, ClientPediStop stopReserved, ClientPediStop stopTaken, ClientPediStop stopDelivered, boolean isReserved, PassengerState state) {
        this.childDetails = childDetails;
        this.stopReserved = stopReserved;
        this.stopTaken = stopTaken;
        this.stopDelivered = stopDelivered;
        this.reserved = isReserved;
        this.state = state;
    }
}
