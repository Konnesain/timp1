package timp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class EmployeeRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Position is required")
    @Size(max = 100)
    private String position;

    private List<Long> buildingAccessIds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public List<Long> getBuildingAccessIds() { return buildingAccessIds; }
    public void setBuildingAccessIds(List<Long> buildingAccessIds) { this.buildingAccessIds = buildingAccessIds; }
}