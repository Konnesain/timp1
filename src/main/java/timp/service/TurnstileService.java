package timp.service;

import timp.dto.TurnstileResponse;
import timp.model.Building;
import timp.model.Employee;
import timp.repository.BuildingRepository;
import timp.repository.EmployeeBuildingAccessRepository;
import timp.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class TurnstileService {

    private final EmployeeRepository employeeRepository;
    private final BuildingRepository buildingRepository;
    private final EmployeeBuildingAccessRepository accessRepository;
    private final SecurityEventLogger eventLogger;

    public TurnstileService(EmployeeRepository employeeRepository,
                            BuildingRepository buildingRepository,
                            EmployeeBuildingAccessRepository accessRepository,
                            SecurityEventLogger eventLogger) {
        this.employeeRepository = employeeRepository;
        this.buildingRepository = buildingRepository;
        this.accessRepository = accessRepository;
        this.eventLogger = eventLogger;
    }

    public TurnstileResponse processAccess(Long employeeId, Long buildingId, String action) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        Building building = buildingRepository.findById(buildingId).orElse(null);

        if (building == null) {
            eventLogger.logAccessAttemptUnknown("Здание ID " + buildingId + " не найдено | Действие: " + action.toUpperCase(), false);
            return new TurnstileResponse(false, "Здание с ID " + buildingId + " не найдено");
        }

        if (employee == null) {
            eventLogger.logAccessAttemptUnknown(building.getName() + " | ID: " + employeeId + ", действие: " + action.toUpperCase(), false);
            return new TurnstileResponse(false, "Сотрудник с ID " + employeeId + " не найден");
        }

        boolean granted = false;
        String message;

        if ("ENTRY".equalsIgnoreCase(action)) {
            if (!accessRepository.existsByEmployeeIdAndBuildingId(employeeId, buildingId)) {
                message = "Отказано: " + employee.getName() + " — нет доступа к зданию «" + building.getName() + "»";
            } else if (employee.isInside()) {
                String currentBuilding = buildingRepository.findById(employee.getBuildingId())
                        .map(Building::getName).orElse("неизвестное здание");
                message = "Отказано: " + employee.getName() + " — уже внутри («" + currentBuilding + "»)";
            } else {
                granted = true;
                employee.setBuildingId(buildingId);
                employeeRepository.save(employee);
                message = "Разрешено: " + employee.getName() + " → " + building.getName();
            }
        } else if ("EXIT".equalsIgnoreCase(action)) {
            if (!employee.isInside()) {
                message = "Отказано: " + employee.getName() + " — не внутри";
            } else if (!employee.getBuildingId().equals(buildingId)) {
                String currentBuilding = buildingRepository.findById(employee.getBuildingId())
                        .map(Building::getName).orElse("неизвестное");
                message = "Отказано: " + employee.getName()
                        + " — находится в другом здании («" + currentBuilding + "»)";
            } else {
                granted = true;
                employee.setBuildingId(null);
                employeeRepository.save(employee);
                message = "Разрешено: " + employee.getName() + " ← " + building.getName();
            }
        } else {
            eventLogger.logAccessAttempt("Неизвестное действие: " + action, "", false, employee.getId().intValue(), employee.getName());
            return new TurnstileResponse(false, "Неизвестное действие: " + action);
        }

        eventLogger.logAccessAttempt(building.getName(), action.toUpperCase(), granted, employee.getId().intValue(), employee.getName());

        return new TurnstileResponse(granted, message);
    }
}