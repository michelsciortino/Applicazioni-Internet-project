package it.polito.ai.project.generalmodels;


import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class ClientReachedStop {
    private String stopName;
    @Min(-1)
    private long arrivalDelay;
    @Min(-1)
    private long departureDelay;

    public ClientReachedStop(String stopName, @Min(0) long arrivalDelay, @Min(0) long departureDelay) {
        this.stopName = stopName;
        this.arrivalDelay = arrivalDelay;
        this.departureDelay = departureDelay;
    }
}

