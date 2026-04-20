package timp.service;

import timp.model.Building;
import timp.model.Employee;
import timp.model.Sensor;
import timp.repository.BuildingRepository;
import timp.repository.EmployeeRepository;
import timp.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class SecurityReportService {

    private final BuildingRepository buildingRepository;
    private final EmployeeRepository employeeRepository;
    private final SensorRepository sensorRepository;

    public SecurityReportService(BuildingRepository buildingRepository,
                                  EmployeeRepository employeeRepository,
                                  SensorRepository sensorRepository) {
        this.buildingRepository = buildingRepository;
        this.employeeRepository = employeeRepository;
        this.sensorRepository = sensorRepository;
    }

    public String generateBuildingsReport() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        sb.append("=====================================\n");
        sb.append("ОТЧЁТ О БЕЗОПАСНОСТИ ЗДАНИЙ\n");
        sb.append("Дата: ").append(LocalDateTime.now().format(formatter)).append("\n");
        sb.append("=====================================\n\n");

        List<Building> buildings = buildingRepository.findAll();
        Map<Long, List<Employee>> employeesByBuilding = employeeRepository.findAll().stream()
                .filter(Employee::isInside)
                .collect(Collectors.groupingBy(Employee::getBuildingId));

        Map<Long, List<Sensor>> sensorsByBuilding = sensorRepository.findAll().stream()
                .collect(Collectors.groupingBy(s -> s.getBuilding().getId()));

        int totalBuildings = buildings.size();
        int enabledBuildings = 0;
        int disabledBuildings = 0;
        int noSensorsBuildings = 0;
        int totalEmployeesInside = 0;

        for (Building building : buildings) {
            List<Sensor> sensors = sensorsByBuilding.getOrDefault(building.getId(), List.of());
            List<Employee> employeesInside = employeesByBuilding.getOrDefault(building.getId(), List.of());

            long onlineSensors = sensors.stream().filter(Sensor::isOnline).count();
            long offlineSensors = sensors.size() - onlineSensors;

            sb.append("ЗДАНИЕ: ").append(building.getName()).append("\n");
            sb.append("  Датчики: ").append(sensors.size())
              .append(" (").append(onlineSensors).append(" онлайн, ")
              .append(offlineSensors).append(" офлайн)").append("\n");
            sb.append("  Сотрудники внутри: ").append(employeesInside.size()).append("\n");

            for (Employee emp : employeesInside) {
                sb.append("    - ").append(emp.getName())
                  .append(" (").append(emp.getPosition()).append(")").append("\n");
            }

            if (employeesInside.isEmpty()) {
                sb.append("\n");
            }

            totalEmployeesInside += employeesInside.size();
        }

        sb.append("=====================================\n");
        sb.append("ИТОГО:\n");
        sb.append("  Зданий: ").append(totalBuildings).append("\n");
        sb.append("  Включено: ").append(enabledBuildings).append("\n");
        sb.append("  Выключено: ").append(disabledBuildings).append("\n");
        sb.append("  Без датчиков: ").append(noSensorsBuildings).append("\n");
        sb.append("  Всего сотрудников внутри: ").append(totalEmployeesInside).append("\n");
        sb.append("=====================================\n");

        return sb.toString();
    }

    public String generateBuildingReport(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Здание не найдено: " + buildingId));

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        sb.append("=====================================\n");
        sb.append("ОТЧЁТ О БЕЗОПАСНОСТИ ЗДАНИЯ\n");
        sb.append("Дата: ").append(LocalDateTime.now().format(formatter)).append("\n");
        sb.append("=====================================\n\n");

        List<Sensor> sensors = sensorRepository.findByBuildingId(buildingId);
        List<Employee> employeesInside = employeeRepository.findAll().stream()
                .filter(e -> e.isInside() && buildingId.equals(e.getBuildingId()))
                .collect(Collectors.toList());

        long onlineSensors = sensors.stream().filter(Sensor::isOnline).count();
        long offlineSensors = sensors.size() - onlineSensors;

        String status;
        if (sensors.isEmpty()) {
            status = "Нет датчиков";
        } else if (offlineSensors > 0) {
            status = "Выключено";
        } else {
            status = "Включено";
        }

        sb.append("ЗДАНИЕ: ").append(building.getName()).append("\n");
        if (building.getDescription() != null && !building.getDescription().isEmpty()) {
            sb.append("Описание: ").append(building.getDescription()).append("\n");
        }
        sb.append("Статус: ").append(status).append("\n");
        sb.append("Датчиков: ").append(sensors.size())
          .append(" (").append(onlineSensors).append(" онлайн, ")
          .append(offlineSensors).append(" офлайн)").append("\n\n");

        sb.append("СОТРУДНИКИ ВНУТРИ: ").append(employeesInside.size()).append("\n");
        if (employeesInside.isEmpty()) {
            sb.append("  Нет сотрудников\n");
        } else {
            for (Employee emp : employeesInside) {
                sb.append("  - ").append(emp.getName())
                  .append(" (").append(emp.getPosition()).append(")\n");
            }
        }

        sb.append("\n=====================================\n");
        sb.append("ИНФОРМАЦИЯ О ДАТЧИКАХ:\n");
        sb.append("=====================================\n");
        if (sensors.isEmpty()) {
            sb.append("  Нет датчиков\n");
        } else {
            for (Sensor sensor : sensors) {
                sb.append("  Датчик: ").append(sensor.getName()).append("\n");
                sb.append("    Тип: ").append(sensor.getType()).append("\n");
                sb.append("    Статус: ").append(sensor.isOnline() ? "Онлайн" : "Офлайн").append("\n");
                if (sensor.hasValue()) {
                    sb.append("    Значение: ").append(sensor.getValue()).append("\n");
                }
                if (sensor.getLastSeen() != null) {
                    sb.append("    Последнее обновление: ").append(sensor.getLastSeen().format(formatter)).append("\n");
                }
                sb.append("\n");
            }
        }

        sb.append("=====================================\n");
        return sb.toString();
    }
}