package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.PediStop;
import it.polito.ai.project.services.database.models.RaceState;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ClientRace {

    private ClientLine line;
    private DirectionType direction;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;
    private RaceState raceState;
    private ClientPediStop currentStop;
    private List<ClientPassenger> passengers;
    private List<ClientCompanion> companions;
    @Nullable
    private ClientCompanion companion;

    public ClientRace(ClientLine line, DirectionType direction, Date date, ClientPediStop currentStop, RaceState raceState, List<ClientPassenger> passengers, List<ClientCompanion> companions, @Nullable ClientCompanion companion) {
        this.line = line;
        this.direction = direction;
        this.date = date;
        this.currentStop = currentStop;
        this.raceState = raceState;
        this.passengers = passengers;
        this.companions = companions;
        this.companion = companion;
    }

    public ClientRace() {
        passengers = new ArrayList<>();
        companions = new ArrayList<>();
    }
}