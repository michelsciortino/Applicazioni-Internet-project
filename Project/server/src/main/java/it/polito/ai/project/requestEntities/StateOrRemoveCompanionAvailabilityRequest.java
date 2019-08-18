package it.polito.ai.project.requestEntities;

import it.polito.ai.project.generalmodels.ClientCompanion;
import it.polito.ai.project.generalmodels.ClientRace;
import lombok.Data;

@Data
public class StateOrRemoveCompanionAvailabilityRequest {
    private ClientCompanion targetCompanion;
    private ClientRace clientRace;

    public StateOrRemoveCompanionAvailabilityRequest(ClientCompanion targetCompanion, ClientRace clientRace) {
        this.targetCompanion = targetCompanion;
        this.clientRace = clientRace;
    }
}
