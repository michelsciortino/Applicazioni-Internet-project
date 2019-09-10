package it.polito.ai.project.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Document(collection = "races")
@Data
@CompoundIndex(def = "{'lineName':1, 'direction':1, 'date':1}", unique = true, name = "raceIndex")
public class Race {
    @Id
    private String id;
    //compound key
    private String lineName;
    private DirectionType direction;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;

    private RaceState raceState;
    private List<Passenger> passengers;

    private List<Companion> companions;

    public Race(String lineName, DirectionType direction, Date date, RaceState raceState, List<Passenger> passengers, List<Companion> companions) {
        this.lineName = lineName;
        this.direction = direction;
        this.date = date;
        this.raceState = raceState;
        this.passengers = passengers;
        this.companions = companions;
    }
}
