package it.polito.ai.lab5.controllers.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Builder
@Data
public class Reservation {
    @Nullable
    private String id;
    @NotNull
    private String childName;
    @NotNull
    private String childCf;
    @NotNull
    private String stopName;
    @NotNull
    private String direction;

    private boolean present;
}
