package timp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_building_access", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "building_id"})
})
public class EmployeeBuildingAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "building_id", nullable = false)
    private Long buildingId;

    public EmployeeBuildingAccess() { }

    public EmployeeBuildingAccess(Long employeeId, Long buildingId) { this.employeeId = employeeId; this.buildingId = buildingId; }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
}