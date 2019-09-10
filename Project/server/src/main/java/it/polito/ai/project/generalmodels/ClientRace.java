package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.RaceState;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ClientRace {

    private String lineName;
    private DirectionType direction;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;
    private RaceState raceState;
    private List<ClientPassenger> passengers;
    private List<ClientCompanion> companions;

    public ClientRace(String lineName, DirectionType direction, Date date, RaceState raceState, List<ClientPassenger> passengers, List<ClientCompanion> companions) {
        this.lineName = lineName;
        this.direction = direction;
        this.date = date;
        this.raceState = raceState;
        this.passengers = passengers;
        this.companions = companions;
    }

    public ClientRace() {
        passengers = new ArrayList<>();
        companions = new ArrayList<>();
    }
}