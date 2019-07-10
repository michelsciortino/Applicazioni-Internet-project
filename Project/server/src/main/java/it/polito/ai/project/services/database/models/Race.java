package it.polito.ai.project.services.database.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@Document(collection = "races")
@Data
@Builder
@CompoundIndex(def = "{'lineName':1, 'direction':1, 'data':1}", unique = true, name = "raceIndex")
public class Race {
    //compound key
    private String lineName;
    private DirectionType direction;
    private Date data;

    private List<Passenger> passengers;

    private List<Companion> companions;
}
