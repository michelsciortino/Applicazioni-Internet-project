package it.polito.ai.project.generalmodels;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClientLine {


        @Indexed(unique = true)
        @Size(min = 1, max = 30)
        private String name;
        @NotNull
        private List<JsonPediStop> outwardStops;
        @NotNull
        private List<JsonPediStop> returnStops;
        private List<JsonChild> subscribedChildren;
        @NotNull
        private List<String> admins;

    public ClientLine(@Size(min = 1, max = 30) String name, @NotNull List<JsonPediStop> outwardStops, @NotNull List<JsonPediStop> returnStops, List<JsonChild> subscribedChildren, @NotNull List<String> admins) {
        this.name = name;
        this.outwardStops = outwardStops;
        this.returnStops = returnStops;
        this.subscribedChildren = subscribedChildren;
        this.admins = admins;
    }
}
