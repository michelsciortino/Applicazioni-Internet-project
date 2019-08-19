package it.polito.ai.project.requestEntities;

import it.polito.ai.project.generalmodels.ClientChild;
import lombok.Data;

import java.util.List;

@Data
public class AddChildToLineRequest {
    ClientChild child;
    String lineName;


    public AddChildToLineRequest(ClientChild child, String lineName) {
        this.child = child;
        this.lineName = lineName;

    }
}
