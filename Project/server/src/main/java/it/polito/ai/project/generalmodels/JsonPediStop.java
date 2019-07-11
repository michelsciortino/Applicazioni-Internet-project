package it.polito.ai.project.generalmodels;

import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@JsonComponent
@Data
public class JsonPediStop {

    @Size(min = 2, max = 30)
    private String name;
    @DecimalMin(value = "-180.0", inclusive = true)
    @DecimalMax(value = "180.0", inclusive = true)
    private BigDecimal longitude;
    @DecimalMin(value = "-90.0", inclusive = true)
    @DecimalMax(value = "90.0", inclusive = true)
    private BigDecimal latitude;
    @Min(0)
    private long delayinmillis;

    public JsonPediStop() {
    }

    public JsonPediStop(@Size(min = 2, max = 30) String name, @DecimalMin(value = "-180.0", inclusive = true) @DecimalMax(value = "180.0", inclusive = true) BigDecimal longitude, @DecimalMin(value = "-90.0", inclusive = true) @DecimalMax(value = "90.0", inclusive = true) BigDecimal latitude, @Min(0) long delayinmillis) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.delayinmillis = delayinmillis;
    }
}

