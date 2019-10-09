package it.polito.ai.project.requestEntities;


import it.polito.ai.project.generalmodels.ClientPediStop;
import it.polito.ai.project.generalmodels.ClientRace;
import it.polito.ai.project.generalmodels.ClientReachedStop;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class StopReachedRequest {
    ClientRace race;
    ClientPediStop pediStopReached;
    @Min(0)
    private long arrivalDelay;


    public StopReachedRequest(ClientRace race, ClientPediStop pediStopReached, long arrivalDelay) {
        this.race = race;
        this.pediStopReached = pediStopReached;
        this.arrivalDelay = arrivalDelay;

    }
}
