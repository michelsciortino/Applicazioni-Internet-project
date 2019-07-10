package it.polito.ai.project.services.database.models;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
public class PediStop {

    private String name;
    @DecimalMin(value = "-180.0", inclusive = true)
    @DecimalMax(value = "180.0", inclusive = true)
    private BigDecimal longitude;
    @DecimalMin(value = "-90.0", inclusive = true)
    @DecimalMax(value = "90.0", inclusive = true)
    private BigDecimal latitude;

    private String time;

    public PediStop() {
    }

    public PediStop(BigDecimal longitude, BigDecimal latitude, String name, String time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.time = time;
    }

}

