package timp.dto;

import java.util.List;

public class FireAccessResponse {

    private Long id;
    private Integer positionX;
    private Integer positionY;
    private Integer width;
    private Integer height;
    private Double angle;
    private Boolean open;
    private List<Long> buildingIds;

    public FireAccessResponse() { }

    public FireAccessResponse(Long id, Integer positionX, Integer positionY,
                              Integer width, Integer height, Double angle, Boolean open, List<Long> buildingIds) {
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.open = open;
        this.buildingIds = buildingIds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
