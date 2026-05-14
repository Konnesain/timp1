package timp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fire_access_building", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"fire_access_id", "building_id"})
})
public class FireAccessBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fire_access_id", nullable = false)
    private Long fireAccessId;

    @Column(name = "building_id", nullable = false)
    private Long buildingId;

    public FireAccessBuilding() { }

    public FireAccessBuilding(Long fireAccessId, Long buildingId) { this.fireAccessId = fireAccessId; this.buildingId = buildingId; }

    public Long getId() { return id; }
    public Long getFireAccessId() { return fireAccessId; }
    public void setFireAccessId(Long fireAccessId) { this.fireAccessId = fireAccessId; }
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
}