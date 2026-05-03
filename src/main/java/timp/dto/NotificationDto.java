package timp.dto;

import java.util.List;

public class NotificationDto {

    private String type;
    private Long buildingId;
    private String buildingName;
    private String sensor;

    public NotificationDto() {
    }

    public NotificationDto(String type, Long buildingId, String buildingName, String sensor) {
        this.type = type;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.sensor = sensor;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getSensor() { return sensor; }
    public void setSensor(String sensor) { this.sensor = sensor; }
}
