package timp.service;

import timp.dto.SensorValueRequest;
import timp.dto.SensorResponse;
import timp.model.Sensor;
import timp.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SensorService {

    private static final double CRITICAL_TEMPERATURE = 50.0;

    private final SensorRepository sensorRepository;
    private final NotificationService notificationService;

    public SensorService(SensorRepository sensorRepository, NotificationService notificationService) {
        this.sensorRepository = sensorRepository;
        this.notificationService = notificationService;
    }

    public List<SensorResponse> getAllSensors() {
        return sensorRepository.findAll().stream()
                .map(SensorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SensorResponse> getSensorsByBuilding(Long buildingId) {
        return sensorRepository.findByBuildingId(buildingId).stream()
                .map(SensorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<SensorResponse> getSensorById(Long id) {
        return sensorRepository.findById(id)
                .map(SensorResponse::fromEntity);
    }

    public boolean hasCriticalSensors(List<Sensor> sensors) {
        return sensors.stream()
                .anyMatch(s -> s.getValue() != null && s.getValue() > CRITICAL_TEMPERATURE);
    }

    @Transactional
    public void receiveReading(Long sensorId, SensorValueRequest request) {
        Optional<Sensor> sensorOpt = sensorRepository.findById(sensorId);

        if (sensorOpt.isEmpty()) {
            return;
        }

        Sensor sensor = sensorOpt.get();
        sensor.setValue(request.getValue());
        sensor.setLastSeen(LocalDateTime.now());
        Sensor saved = sensorRepository.save(sensor);

        if (saved.getValue() != null && saved.getValue() > CRITICAL_TEMPERATURE) {
            String sensorInfo = sensor.getName() + ": " + String.format("%.1f°C", sensor.getValue());
            notificationService.sendCriticalAlert(sensor.getBuilding().getId(), sensor.getBuilding().getName(), sensorInfo);
        }

        List<Sensor> buildingSensors = sensorRepository.findByBuildingId(sensor.getBuilding().getId());
        boolean isCritical = hasCriticalSensors(buildingSensors);
        notificationService.resetAlertIfResolved(sensor.getBuilding().getId(), isCritical);
    }
}
