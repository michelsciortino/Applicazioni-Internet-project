package it.polito.ai.project.generalmodels;

import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@JsonComponent
@Data
public class JsonLine {

    @Size(min = 1, max = 30)
    private String name;

    private List<JsonPediStop> outwardStops;
    private List<JsonPediStop> returnStops;
    private List<JsonChild> subscribedChildren;
    private List<String> admins;

    public JsonLine() {
        outwardStops = new ArrayList<>();
        returnStops = new ArrayList<>();
        subscribedChildren = new ArrayList<>();
        admins = new ArrayList<>();
    }

    public JsonLine(@Size(min = 2, max = 30) String name, List<JsonPediStop> outwardStops, List<JsonPediStop> returnStops, List<String> admins) {
        this.name = name;
        this.outwardStops = outwardStops;
        this.returnStops = returnStops;
        this.admins = admins;
        this.subscribedChildren = new ArrayList<>();

    }
}
