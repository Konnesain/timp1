package timp.dto;

public class RoadResponse {

    private Long id;
    private String name;
    private Integer positionX;
    private Integer positionY;
    private Integer width;
    private Integer height;
    private Double angle;
    private String description;

    public RoadResponse() { }

    public RoadResponse(Long id, String name, Integer positionX, Integer positionY,
                        Integer width, Integer height, Double angle, String description) {
        this.id = id;
        this.name = name;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
