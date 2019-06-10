package it.polito.ai.lab5.controllers.models;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Builder
@Data
public class LineReservations {
    private Map<String, Collection<String>> outwardStopsReservations;
    private Map<String, Collection<String>> backStopsReservations;
}
