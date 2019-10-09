package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.CompanionState;
import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
@Data
public class JsonCompanion {
    private JsonUser userDetails;
    private JsonPediStop initialStop;
    private JsonPediStop finalStop;
    private CompanionState state;

    public JsonCompanion() {
    }

    public JsonCompanion(JsonUser userDetails, JsonPediStop initialStop, JsonPediStop finalStop, CompanionState state) {
        this.userDetails = userDetails;
        this.initialStop = initialStop;
        this.finalStop = finalStop;
        this.state = state;
    }
}