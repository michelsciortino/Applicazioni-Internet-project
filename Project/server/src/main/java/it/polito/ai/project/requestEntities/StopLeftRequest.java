package it.polito.ai.project.requestEntities;

import it.polito.ai.project.generalmodels.ClientPediStop;
import it.polito.ai.project.generalmodels.ClientRace;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class StopLeftRequest {
    ClientRace race;
    ClientPediStop pediStopReached;
    @Min(0)
    private long departureDelay;


    public StopLeftRequest(ClientRace race, ClientPediStop pediStopReached, long departureDelay) {
        this.race = race;
        this.pediStopReached = pediStopReached;
        this.departureDelay = departureDelay;

    }
}
