package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.DirectionType;
import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@JsonComponent
@Data
public class JsonRace {

    @Size(min = 2, max = 30)
    private String lineName;
    private DirectionType direction;
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    @NotNull
    private Date data;

    private List<JsonPassenger> passengers;

    private List<JsonCompanion> companions;

    public JsonRace() {
    }

    public JsonRace(@Size(min = 2, max = 30) String lineName, DirectionType direction, @NotNull Date data, List<JsonPassenger> passengers, List<JsonCompanion> companions) {
        this.lineName = lineName;
        this.direction = direction;
        this.data = data;
        this.passengers = passengers;
        this.companions = companions;
    }
}
