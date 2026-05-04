package timp.dto;

import jakarta.validation.constraints.NotBlank;

public class RoadRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private Integer positionX = 0;
    private Integer positionY = 0;
    private Integer width = 100;
    private Integer height = 10;
    private Double angle = 0.0;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPositionX() { return positionX; }
    public void setPositionX(Integer positionX) { this.positionX = positionX; }
    public Integer getPositionY() { return positionY; }
    public void setPositionY(Integer positionY) { this.positionY = positionY; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public Double getAngle() { return angle; }
    public void setAngle(Double angle) { this.angle = angle; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
