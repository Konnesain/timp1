package timp.dto;

import java.util.ArrayList;
import java.util.List;

public class FireAccessRequest {

    private Integer positionX = 0;
    private Integer positionY = 0;
    private Integer width = 30;
    private Integer height = 30;
    private Double angle = 0.0;
    private Boolean open = true;
    private List<Long> buildingIds = new ArrayList<>();

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
    public Boolean getOpen() { return open; }
    public void setOpen(Boolean open) { this.open = open; }
    public List<Long> getBuildingIds() { return buildingIds; }
    public void setBuildingIds(List<Long> buildingIds) { this.buildingIds = buildingIds; }
}
