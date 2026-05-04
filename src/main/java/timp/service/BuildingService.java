package timp.service;

import timp.dto.BuildingDetailsResponse;
import timp.dto.BuildingRequest;
import timp.dto.BuildingResponse;
import timp.dto.EmployeeInfo;
import timp.model.Building;
import timp.model.Employee;
import timp.model.EmployeeBuildingAccess;
import timp.model.Sensor;
import timp.repository.BuildingRepository;
import timp.repository.EmployeeBuildingAccessRepository;
import timp.repository.EmployeeRepository;
import timp.repository.SensorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeBuildingAccessRepository accessRepository;
    private final SensorRepository sensorRepository;
    private final SensorService sensorService;
    private final SecurityEventLogger eventLogger;

    public BuildingService(BuildingRepository buildingRepository,
                           EmployeeRepository employeeRepository,
                           EmployeeBuildingAccessRepository accessRepository,
                           SensorRepository sensorRepository,
                           SensorService sensorService,
                           SecurityEventLogger eventLogger) {
        this.buildingRepository = buildingRepository;
        this.employeeRepository = employeeRepository;
        this.accessRepository = accessRepository;
        this.sensorRepository = sensorRepository;
        this.sensorService = sensorService;
        this.eventLogger = eventLogger;
    }

    @Transactional(readOnly = true)
    public List<BuildingResponse> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BuildingDetailsResponse getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Здание не найдено: " + id));
        eventLogger.logBuildingView(building.getName());

        List<Employee> employeesInside = employeeRepository.findAll().stream()
                .filter(e -> id.equals(e.getBuildingId()))
                .collect(Collectors.toList());

        List<Long> accessEmployeeIds = accessRepository.findByBuildingId(id).stream()
                .map(EmployeeBuildingAccess::getEmployeeId)
                .collect(Collectors.toList());

        List<Employee> employeesWithAccess = employeeRepository.findAllById(accessEmployeeIds);

        return new BuildingDetailsResponse(
                building.getId(),
                building.getName(),
                building.getPositionX(),
                building.getPositionY(),
                building.getWidth(),
                building.getHeight(),
                building.getDescription(),
                employeesInside.size(),
                employeesInside.stream().map(e -> new EmployeeInfo(e.getId(), e.getName(), e.getPosition())).collect(Collectors.toList()),
                employeesWithAccess.stream().map(e -> new EmployeeInfo(e.getId(), e.getName(), e.getPosition())).collect(Collectors.toList())
        );
    }

    public BuildingResponse updateBuilding(Long id, BuildingRequest request) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Здание не найдено: " + id));
        String oldName = building.getName();
        building.setName(request.getName());
        building.setDescription(request.getDescription());
        building.setPositionX(request.getPositionX());
        building.setPositionY(request.getPositionY());
        building.setWidth(request.getWidth());
        building.setHeight(request.getHeight());
        Building saved = buildingRepository.save(building);
        eventLogger.logBuildingEdit(oldName);
        return toResponse(saved);
    }

    public BuildingResponse createBuilding(BuildingRequest request) {
        Building building = new Building();
        building.setName(request.getName());
        building.setDescription(request.getDescription());
        building.setPositionX(request.getPositionX());
        building.setPositionY(request.getPositionY());
        building.setWidth(request.getWidth());
        building.setHeight(request.getHeight());
        Building saved = buildingRepository.save(building);
        eventLogger.logBuildingCreate(building.getName());
        return toResponse(saved);
    }

    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Здание не найдено: " + id);
        }
        Building building = buildingRepository.findById(id).orElse(null);
        String buildingName = building != null ? building.getName() : "ID: " + id;
        sensorRepository.deleteByBuildingId(id);
        accessRepository.deleteByBuildingId(id);
        buildingRepository.deleteById(id);
        eventLogger.logBuildingDelete(buildingName);
    }

    private BuildingResponse toResponse(Building building) {
        List<Sensor> sensors = sensorRepository.findByBuildingId(building.getId());
        BuildingResponse.SensorStatus status = calculateSensorStatus(sensors);

        return new BuildingResponse(
                building.getId(),
                building.getName(),
                building.getPositionX(),
                building.getPositionY(),
                building.getWidth(),
                building.getHeight(),
                building.getDescription(),
                status
        );
    }

    private BuildingResponse.SensorStatus calculateSensorStatus(List<Sensor> sensors) {
        if (sensors.isEmpty()) {
            return BuildingResponse.SensorStatus.NO_SENSORS;
        }

        if (sensorService.hasCriticalSensors(sensors)) {
            return BuildingResponse.SensorStatus.CRITICAL;
        }

        boolean hasOffline = sensors.stream().anyMatch(s -> !s.isOnline());
        if (hasOffline) {
            return BuildingResponse.SensorStatus.WARNING;
        }

        return BuildingResponse.SensorStatus.OK;
    }
}