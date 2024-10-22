package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.RaceState;
import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull
    private Date date;
    private JsonPediStop currentStop;
    private List<JsonPassenger> passengers;
    private List<JsonCompanion> companions;
    @Nullable
    private RaceState raceState;

    public JsonRace() {
    }

    public JsonRace(@Size(min = 2, max = 30) String lineName, DirectionType direction, @NotNull Date date, JsonPediStop currentStop, List<JsonPassenger> passengers, List<JsonCompanion> companions) {
        this.lineName = lineName;
        this.direction = direction;
        this.date = date;
        this.currentStop = currentStop;
        this.passengers = passengers;
        this.companions = companions;
    }
}
