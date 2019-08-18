package it.polito.ai.project.requestEntities;

import it.polito.ai.project.generalmodels.ClientPassenger;
import it.polito.ai.project.generalmodels.ClientPediStop;
import it.polito.ai.project.generalmodels.ClientRace;
import lombok.Data;

import java.util.List;

@Data

public class TakeorDeliverChildrenRequest {
    private ClientRace clientRace;
    private List<ClientPassenger> children;
    //atarting or arrival ClientPediStop;
    private ClientPediStop pedistop;

    public TakeorDeliverChildrenRequest(ClientRace clientRace, List<ClientPassenger> children, ClientPediStop pedistop) {
        this.clientRace = clientRace;
        this.children = children;
        this.pedistop = pedistop;
    }
}
