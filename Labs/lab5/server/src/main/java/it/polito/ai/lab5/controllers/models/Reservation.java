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
    @NotNull
    private boolean present;

    public Reservation(@Nullable String id, @NotNull String childName, @NotNull String childCf, @NotNull String stopName, @NotNull String direction, @NotNull boolean present) {
        this.id = null;
        this.childName = childName;
        this.childCf = childCf;
        this.stopName = stopName;
        this.direction = direction;
        this.present = present;
    }
}
