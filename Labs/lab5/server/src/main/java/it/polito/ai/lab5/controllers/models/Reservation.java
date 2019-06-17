package it.polito.ai.lab5.controllers.models;


import it.polito.ai.lab5.controllers.validators.ValidateDirection;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
public class Reservation {
    @Nullable
    private String id;
    @NotNull
    @Size(min = 2, max = 50)
    private String childName;
    @NotNull
    @Size(min = 2, max = 50)
    private String childSurname;
    @NotNull
    @Size(min = 2, max = 50)
    private String childCf;
    @NotNull
    @Size(min = 2, max = 50)
    @Email
    private String parentUsername;
    @NotNull
    @Size(min = 2, max = 50)
    private String stopName;
    @NotNull
    @Size(min = 2, max = 50)

    @ValidateDirection(acceptedValues = {DirectionType.OUTWARD, DirectionType.RETURN}, message = "Invalid direction")
    private String direction;
    @NotNull
    private boolean present;

    public Reservation(String id, @NotNull String childSurname, @NotNull String childName, @NotNull String childCf, @NotNull String parentUsername, @NotNull String stopName, @NotNull String direction, @NotNull boolean present) {
        this.id = id;
        this.childName = childName;
        this.childCf = childCf;
        this.childSurname = childSurname;
        this.parentUsername = parentUsername;
        this.stopName = stopName;
        this.direction = direction;
        this.present = present;
    }

}
