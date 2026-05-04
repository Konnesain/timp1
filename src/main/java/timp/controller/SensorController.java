package timp.controller;

import timp.dto.SensorValueRequest;
import timp.dto.SensorResponse;
import timp.model.Sensor.SensorType;
import timp.repository.SensorRepository;
import timp.service.SensorService;
import timp.service.SensorSimulator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;
    private final SensorRepository sensorRepository;
    private final SensorSimulator sensorSimulator;

    public SensorController(SensorService sensorService, SensorRepository sensorRepository, SensorSimulator sensorSimulator) {
        this.sensorService = sensorService;
        this.sensorRepository = sensorRepository;
        this.sensorSimulator = sensorSimulator;
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

        sensorService.receiveReading(id, request);
        return sensorService.getSensorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/ignite/{buildingId}")
    public ResponseEntity<List<SensorResponse>> igniteBuilding(@PathVariable Long buildingId) {
        sensorSimulator.igniteBuilding(buildingId);
        return ResponseEntity.ok(sensorRepository.findByBuildingId(buildingId).stream()
                .filter(s -> s.getType() == SensorType.TEMPERATURE)
                .map(SensorResponse::fromEntity)
                .toList());
    }

    @PostMapping("/extinguish/{buildingId}")
    public ResponseEntity<List<SensorResponse>> extinguishBuilding(@PathVariable Long buildingId) {
        sensorSimulator.extinguishBuilding(buildingId);
        return ResponseEntity.ok(sensorRepository.findByBuildingId(buildingId).stream()
                .filter(s -> s.getType() == SensorType.TEMPERATURE)
                .map(SensorResponse::fromEntity)
                .toList());
    }
}
