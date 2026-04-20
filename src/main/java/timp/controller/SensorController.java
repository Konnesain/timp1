package timp.controller;

import timp.dto.SensorValueRequest;
import timp.dto.SensorResponse;
import timp.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
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
}
