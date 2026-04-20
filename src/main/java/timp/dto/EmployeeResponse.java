package timp.dto;

import java.util.List;

public class EmployeeResponse {

    private Long id;
    private String name;
    private String position;
    private List<Long> buildingAccessIds;
    private Long buildingId;
    private String buildingName;

    public EmployeeResponse(Long id, String name, String position,
                            List<Long> buildingAccessIds,
                            Long buildingId, String buildingName) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.buildingAccessIds = buildingAccessIds;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public List<Long> getBuildingAccessIds() { return buildingAccessIds; }
    public Long getBuildingId() { return buildingId; }
    public String getBuildingName() { return buildingName; }
}