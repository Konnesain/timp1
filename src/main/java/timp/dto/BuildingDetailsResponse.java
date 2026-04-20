package timp.dto;

import java.util.List;

public class BuildingDetailsResponse {
    private final Long id;
    private final String name;
    private final Integer positionX;
    private final Integer positionY;
    private final Integer width;
    private final Integer height;
    private final String description;
    private final int employeesInsideCount;
    private final List<EmployeeInfo> employeesInside;
    private final List<EmployeeInfo> employeesWithAccess;

    public BuildingDetailsResponse(Long id, String name, Integer positionX, Integer positionY,
                                   Integer width, Integer height, String description,
                                   int employeesInsideCount, List<EmployeeInfo> employeesInside,
                                   List<EmployeeInfo> employeesWithAccess) {
        this.id = id;
        this.name = name;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.description = description;
        this.employeesInsideCount = employeesInsideCount;
        this.employeesInside = employeesInside;
        this.employeesWithAccess = employeesWithAccess;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getPositionX() { return positionX; }
    public Integer getPositionY() { return positionY; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
    public String getDescription() { return description; }
    public int getEmployeesInsideCount() { return employeesInsideCount; }
    public List<EmployeeInfo> getEmployeesInside() { return employeesInside; }
    public List<EmployeeInfo> getEmployeesWithAccess() { return employeesWithAccess; }
}