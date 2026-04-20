package timp.dto;

public class BuildingResponse {

    public enum SensorStatus {
        OK("В норме"),
        WARNING("Требует проверки"),
        NO_SENSORS("Нет датчиков");

        private final String label;

        SensorStatus(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private final Long id;
    private final String name;
    private final Integer positionX;
    private final Integer positionY;
    private final Integer width;
    private final Integer height;
    private final String description;
    private final SensorStatus sensorStatus;

    public BuildingResponse(Long id, String name, Integer positionX, Integer positionY,
                            Integer width, Integer height, String description,
                            SensorStatus sensorStatus) {
        this.id = id;
        this.name = name;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.description = description;
        this.sensorStatus = sensorStatus;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getPositionX() { return positionX; }
    public Integer getPositionY() { return positionY; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
    public String getDescription() { return description; }
    public SensorStatus getSensorStatus() { return sensorStatus; }
}