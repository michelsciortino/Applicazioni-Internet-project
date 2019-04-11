package it.polito.ai.labs.lab2.models.rest;

import lombok.Builder;

import java.util.Collection;
import java.util.Map;

@Builder
public class LineReservations {
    Map<String,Collection<String>> outwardStopsReservations;
    Map<String,Collection<String>> backStopsReservations;
}
