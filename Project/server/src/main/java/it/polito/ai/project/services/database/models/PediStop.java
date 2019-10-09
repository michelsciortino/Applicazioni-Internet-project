package it.polito.ai.project.services.database.models;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
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
    @Min(0)
    private long delayInMillis;

    public PediStop() {
    }

    public PediStop(String name, @DecimalMin(value = "-180.0", inclusive = true) @DecimalMax(value = "180.0", inclusive = true) BigDecimal longitude, @DecimalMin(value = "-90.0", inclusive = true) @DecimalMax(value = "90.0", inclusive = true) BigDecimal latitude, @Min(0) long delayInMillis) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.delayInMillis = delayInMillis;
    }
}

