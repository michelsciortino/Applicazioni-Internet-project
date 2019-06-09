package it.polito.ai.lab5.controllers.models;

import lombok.Builder;

import java.util.Collection;
import java.util.Map;

@Builder
public class LineReservations {
    private Map<String, Collection<String>> outwardStopsReservations;
    private Map<String, Collection<String>> backStopsReservations;
}
