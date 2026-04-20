package timp.dto;

public class EmployeeInfo {
    private final Long id;
    private final String name;
    private final String position;

    public EmployeeInfo(Long id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
}