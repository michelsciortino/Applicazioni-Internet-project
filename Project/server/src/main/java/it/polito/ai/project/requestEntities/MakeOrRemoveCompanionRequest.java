package it.polito.ai.project.requestEntities;

import lombok.Data;

@Data
public class MakeOrRemoveCompanionRequest {

    private String targetName;

    public MakeOrRemoveCompanionRequest(String targetName) {
        this.targetName = targetName;
    }
    public MakeOrRemoveCompanionRequest(){
        this.targetName = null;
    }
}
