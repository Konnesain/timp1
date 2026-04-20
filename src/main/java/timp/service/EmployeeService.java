package timp.service;

import timp.dto.EmployeeRequest;
import timp.dto.EmployeeResponse;
import timp.model.Building;
import timp.model.Employee;
import timp.model.EmployeeBuildingAccess;
import timp.repository.BuildingRepository;
import timp.repository.EmployeeBuildingAccessRepository;
import timp.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BuildingRepository buildingRepository;
    private final EmployeeBuildingAccessRepository accessRepository;
    private final SecurityEventLogger eventLogger;

    public EmployeeService(EmployeeRepository employeeRepository,
                           BuildingRepository buildingRepository,
                           EmployeeBuildingAccessRepository accessRepository,
                           SecurityEventLogger eventLogger) {
        this.employeeRepository = employeeRepository;
        this.buildingRepository = buildingRepository;
        this.accessRepository = accessRepository;
        this.eventLogger = eventLogger;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        eventLogger.logEmployeeRequest();
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден: " + id));
        eventLogger.logEmployeeView(id, employee.getName());
        return toResponse(employee);
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = new Employee(request.getName(), request.getPosition());
        Employee savedEmployee = employeeRepository.save(employee);

        updateBuildingAccess(savedEmployee.getId(), request.getBuildingAccessIds());

        eventLogger.logEmployeeCreate(savedEmployee.getName(), savedEmployee.getId(), savedEmployee.getPosition());

        return toResponse(savedEmployee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден: " + id));

        String oldInfo = employee.getName() + " — " + employee.getPosition();

        employee.setName(request.getName());
        employee.setPosition(request.getPosition());

        Employee updatedEmployee = employeeRepository.save(employee);

        updateBuildingAccess(updatedEmployee.getId(), request.getBuildingAccessIds());

        String newInfo = updatedEmployee.getName() + " — " + updatedEmployee.getPosition();
        eventLogger.logEmployeeEdit(id, oldInfo, newInfo);

        return toResponse(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден: " + id);
        }

        Employee employee = employeeRepository.findById(id).orElse(null);
        String name = employee != null ? employee.getName() : "ID: " + id;

        accessRepository.deleteByEmployeeId(id);
        employeeRepository.deleteById(id);

        eventLogger.logEmployeeDelete(id, name);
    }

    private void updateBuildingAccess(Long employeeId, List<Long> buildingIds) {
        List<EmployeeBuildingAccess> currentAccess = accessRepository.findByEmployeeId(employeeId);
        List<Long> currentBuildingIds = currentAccess.stream()
                .map(EmployeeBuildingAccess::getBuildingId)
                .collect(Collectors.toList());

        List<Long> newBuildingIds = buildingIds != null ? buildingIds : List.of();

        List<Long> toRemove = currentBuildingIds.stream()
                .filter(id -> !newBuildingIds.contains(id))
                .toList();

        List<Long> toAdd = newBuildingIds.stream()
                .filter(id -> !currentBuildingIds.contains(id))
                .toList();

        for (Long buildingId : toRemove) {
            accessRepository.findByEmployeeIdAndBuildingId(employeeId, buildingId)
                    .ifPresent(accessRepository::delete);
        }

        for (Long buildingId : toAdd) {
            accessRepository.save(new EmployeeBuildingAccess(employeeId, buildingId));
        }
    }

    private EmployeeResponse toResponse(Employee employee) {
        List<Long> buildingAccessIds = accessRepository.findByEmployeeId(employee.getId())
                .stream()
                .map(EmployeeBuildingAccess::getBuildingId)
                .collect(Collectors.toList());

        String buildingName = null;
        if (employee.getBuildingId() != null) {
            buildingName = buildingRepository.findById(employee.getBuildingId())
                    .map(Building::getName)
                    .orElse(null);
        }
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getPosition(),
                buildingAccessIds,
                employee.getBuildingId(),
                buildingName
        );
    }
}