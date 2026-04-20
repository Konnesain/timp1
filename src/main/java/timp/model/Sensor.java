package timp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensors")
public class Sensor {

    public enum SensorType {
        TEMPERATURE("Температура"),
        CAMERA("Камера");

        private final String label;
        SensorType(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(nullable = false)
    private String name;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "sensor_value")
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType type = SensorType.TEMPERATURE;

    public Sensor() { }

    public Sensor(Building building, String name) { this.building = building; this.name = name; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public SensorType getType() { return type; }
    public void setType(SensorType type) { this.type = type; }

    public boolean isOnline() { return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusSeconds(10)); }
    public boolean hasValue() { return type == SensorType.TEMPERATURE && value != null; }
}