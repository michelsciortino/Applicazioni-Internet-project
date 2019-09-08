package it.polito.ai.project.requestEntities;

import lombok.Data;

@Data
public class MakeCompanionRequest {

    private String targetName;

    public MakeCompanionRequest(String targetName) {
        this.targetName = targetName;
    }
    public MakeCompanionRequest(){
        this.targetName = null;
    }
}
