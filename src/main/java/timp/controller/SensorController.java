package timp.controller;

import timp.dto.SensorValueRequest;
import timp.dto.SensorResponse;
import timp.model.Sensor;
import timp.repository.SensorRepository;
import timp.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;
    private final SensorRepository sensorRepository;

    public SensorController(SensorService sensorService, SensorRepository sensorRepository) {
        this.sensorService = sensorService;
        this.sensorRepository = sensorRepository;
    }

    @GetMapping
    public ResponseEntity<List<SensorResponse>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getAllSensors());
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<SensorResponse>> getSensorsByBuilding(@PathVariable Long buildingId) {
        return ResponseEntity.ok(sensorService.getSensorsByBuilding(buildingId));
    }

    @PostMapping("/{id}/readings")
    public ResponseEntity<SensorResponse> receiveReading(
            @PathVariable Long id,
            @Valid @RequestBody SensorValueRequest request) {
        
        return sensorService.receiveReading(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/ignite/{buildingId}")
    public ResponseEntity<List<SensorResponse>> igniteBuilding(@PathVariable Long buildingId) {
        List<Sensor> sensors = sensorRepository.findByBuildingId(buildingId).stream()
                .filter(s -> s.getType() == Sensor.SensorType.TEMPERATURE)
                .toList();
        sensorService.setCriticalModeForBuilding(sensors);
        return ResponseEntity.ok(sensors.stream()
                .map(SensorResponse::fromEntity)
                .toList());
    }

    @PostMapping("/extinguish/{buildingId}")
    public ResponseEntity<List<SensorResponse>> extinguishBuilding(@PathVariable Long buildingId) {
        List<Sensor> sensors = sensorRepository.findByBuildingId(buildingId).stream()
                .filter(s -> s.getType() == Sensor.SensorType.TEMPERATURE)
                .toList();
        sensorService.removeCriticalModeForBuilding(sensors);
        return ResponseEntity.ok(sensors.stream()
                .map(SensorResponse::fromEntity)
                .toList());
    }
}
