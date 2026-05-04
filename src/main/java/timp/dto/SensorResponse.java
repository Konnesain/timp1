package timp.dto;

import timp.model.Sensor;
import timp.model.Sensor.SensorType;
import java.time.LocalDateTime;

public class SensorResponse {

    private final Long id;
    private final Long buildingId;
    private final String buildingName;
    private final String name;
    private final SensorType type;
    private final boolean online;
    private final Double value;
    private final LocalDateTime lastSeen;

    public SensorResponse(Long id, Long buildingId, String buildingName, String name,
                          SensorType type, boolean online, Double value, LocalDateTime lastSeen) {
        this.id = id;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.name = name;
        this.type = type;
        this.online = online;
        this.value = value;
        this.lastSeen = lastSeen;
    }

    public static SensorResponse fromEntity(Sensor sensor) {
        return new SensorResponse(
                sensor.getId(),
                sensor.getBuilding().getId(),
                sensor.getBuilding().getName(),
                sensor.getName(),
                sensor.getType(),
                sensor.isOnline(),
                sensor.getValue(),
                sensor.getLastSeen()
        );
    }

    public Long getId() { return id; }
    public Long getBuildingId() { return buildingId; }
    public String getBuildingName() { return buildingName; }
    public String getName() { return name; }
    public SensorType getType() { return type; }
    public boolean isOnline() { return online; }
    public Double getValue() { return value; }
    public LocalDateTime getLastSeen() { return lastSeen; }
}
