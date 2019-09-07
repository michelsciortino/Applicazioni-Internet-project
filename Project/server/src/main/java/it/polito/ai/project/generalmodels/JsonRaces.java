package it.polito.ai.project.generalmodels;

import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class JsonRaces {
    public List<JsonRace> races = new ArrayList<>();

    public JsonRaces(List<JsonRace> races) {
        this.races = races;
    }

    public JsonRaces() {
    }
}
