package it.polito.ai.project.requestEntities;

import it.polito.ai.project.generalmodels.ClientRace;
import lombok.Data;

import java.util.List;

@Data
public class SelectCompanionsRequest {
    private ClientRace clientRace;
    private List<String> companions;
}
