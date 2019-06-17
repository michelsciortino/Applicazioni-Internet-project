package it.polito.ai.lab5.files.json;

import it.polito.ai.lab5.services.database.models.LineSubscribedChild;
import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;

@JsonComponent
//@Builder
@Data
public class Line {
    public String name;
    public ArrayList<PediStop> outboundStops;
    public ArrayList<PediStop> returnStops;
    public ArrayList<LineSubscribedChild> subscribedChildren;

    public Line() {
    }

    public Line(String name, ArrayList<PediStop> outboundStops, ArrayList<PediStop> returnStops, ArrayList<LineSubscribedChild> subscribedChildren) {
        this.name = name;
        this.outboundStops = outboundStops;
        this.returnStops = returnStops;
        this.subscribedChildren = subscribedChildren;
    }

}

