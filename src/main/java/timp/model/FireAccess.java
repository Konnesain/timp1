package timp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fire_access")
public class FireAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position_x")
    private Integer positionX = 0;

    @Column(name = "position_y")
    private Integer positionY = 0;

    private Integer width = 40;

    private Integer height = 40;

    private Double angle = 0.0;

    private Boolean open = false;

    public FireAccess() { }

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
}
