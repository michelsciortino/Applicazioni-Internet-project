package it.polito.ai.project.requestEntities;

import it.polito.ai.project.generalmodels.ClientPassenger;
import it.polito.ai.project.generalmodels.ClientRace;
import lombok.Data;

import java.util.List;

@Data
public class ReserveChildrenRequest {

     private ClientRace clientRace;
     private List<ClientPassenger> children;

    public ReserveChildrenRequest(ClientRace clientRace, List<ClientPassenger> children) {
        this.clientRace = clientRace;
        this.children = children;
    }
}
