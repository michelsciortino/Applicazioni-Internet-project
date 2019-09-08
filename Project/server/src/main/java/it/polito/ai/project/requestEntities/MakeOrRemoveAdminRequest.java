package it.polito.ai.project.requestEntities;

import lombok.Data;

@Data
public class MakeOrRemoveAdminRequest {

    private String targetName;
    private String lineName;

    public MakeOrRemoveAdminRequest(String targetName, String lineName) {
        this.targetName = targetName;
        this.lineName = lineName;
    }
}
