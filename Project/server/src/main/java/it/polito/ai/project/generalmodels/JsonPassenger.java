package it.polito.ai.project.generalmodels;

import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
@Data
public class JsonPassenger {
    private JsonChild childDetails;
    private boolean isReserved;
    private boolean isPresent;

    public JsonPassenger() {
        isReserved = false;
        isPresent = false;
    }

    public JsonPassenger(JsonChild childDetails, boolean isReserved, boolean isPresent) {
        this.childDetails = childDetails;
        this.isReserved = isReserved;
        this.isPresent = isPresent;
    }
}
