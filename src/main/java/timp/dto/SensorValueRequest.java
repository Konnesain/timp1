package timp.dto;

import jakarta.validation.constraints.NotNull;

public class SensorValueRequest {

    @NotNull(message = "Value is required")
    private Double value;

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
}