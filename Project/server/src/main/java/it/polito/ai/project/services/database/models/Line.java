package it.polito.ai.project.services.database.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "lines")
@Data
public class Line {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String name;

    @NotNull
    private List<PediStop> outwardStops;
    @NotNull
    private List<PediStop> returnStops;
    private List<Child> subscribedChildren;
    // TO-DO gestire inserimento e cancellazione nuovi admin
    @NotNull
    private List<String> admins;

    public Line() {
        outwardStops = new ArrayList<>();
        returnStops = new ArrayList<>();
        subscribedChildren = new ArrayList<>();
        admins = new ArrayList<>();
    }

    public Line(String name, @NotNull ArrayList<PediStop> outwardStops, @NotNull ArrayList<PediStop> returnStops, @NotNull List<String> admins) {
        this.name = name;
        this.outwardStops = outwardStops;
        this.returnStops = returnStops;
        this.admins = admins;
        this.subscribedChildren = new ArrayList<>();
    }

}
