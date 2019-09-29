package it.polito.ai.project.services.database.models;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class ReachedStop {
    private String stopName;
    @Min(0)
    private long arrivalDelay;
    @Min(0)
    private long departureDelay;

    public ReachedStop(String stopName, @Min(0) long arrivalDelay, @Min(0) long departureDelay) {
        this.stopName = stopName;
        this.arrivalDelay = arrivalDelay;
        this.departureDelay = departureDelay;
    }
}
