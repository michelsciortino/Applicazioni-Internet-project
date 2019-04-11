package it.polito.ai.labs.lab2.models.json;

import lombok.Builder;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
@Builder
public class PediStop {
    public float longitude;
    public float latitude;
    public String name;
}
