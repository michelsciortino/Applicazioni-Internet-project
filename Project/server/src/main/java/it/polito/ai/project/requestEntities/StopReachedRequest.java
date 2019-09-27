package it.polito.ai.project.requestEntities;


import it.polito.ai.project.generalmodels.ClientPediStop;
import it.polito.ai.project.generalmodels.ClientRace;

public class StopReachedRequest {
    ClientRace race;
    ClientPediStop pediStopReached;

    public StopReachedRequest(ClientRace race, ClientPediStop pediStopReached) {
        this.race = race;
        this.pediStopReached = pediStopReached;
    }
}
