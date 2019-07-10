package it.polito.ai.project.services.database.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "lines")
@Data
public class Line {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String name;

    private List<PediStop> outwardStops;
    private List<PediStop> returnStops;
    private List<Child> subscribedChildren;

    public Line() {
        outwardStops = new ArrayList<>();
        returnStops = new ArrayList<>();
        subscribedChildren = new ArrayList<>();
    }

    public Line(String name, ArrayList<PediStop> outwardStops, ArrayList<PediStop> returnStops) {
        this.name = name;
        this.outwardStops = outwardStops;
        this.returnStops = returnStops;
        this.subscribedChildren = new ArrayList<>();
    }
}
