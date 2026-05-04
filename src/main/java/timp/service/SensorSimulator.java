package timp.service;

import timp.model.Sensor;
import timp.model.Sensor.SensorType;
import timp.repository.SensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SensorSimulator {

    private static final Logger log = LoggerFactory.getLogger(SensorSimulator.class);
    private static final double BASE_TEMPERATURE = 22.0;
    private static final double TEMPERATURE_VARIANCE = 2.0;
    private static final double CRITICAL_TEMPERATURE = 50.0;

    private final SensorRepository sensorRepository;
    private final NotificationService notificationService;
    private final Random random = new Random();
    private final Set<Long> criticalSensorIds = ConcurrentHashMap.newKeySet();

    public SensorSimulator(SensorRepository sensorRepository, NotificationService notificationService) {
        this.sensorRepository = sensorRepository;
        this.notificationService = notificationService;
    }

    public void setCriticalModeForBuilding(List<Sensor> sensors) {
        sensors.stream()
                .filter(s -> s.getType() == SensorType.TEMPERATURE)
                .forEach(s -> criticalSensorIds.add(s.getId()));
        log.info("Ignited {} temperature sensors", criticalSensorIds.size());
    }

    public void removeCriticalModeForBuilding(List<Sensor> sensors) {
        sensors.stream()
                .filter(s -> s.getType() == SensorType.TEMPERATURE)
                .forEach(s -> criticalSensorIds.remove(s.getId()));
        log.info("Extinguished {} temperature sensors", sensors.size());
    }

    public boolean isCritical(Long sensorId) {
        return criticalSensorIds.contains(sensorId);
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void simulateReadings() {
        List<Sensor> sensors = sensorRepository.findAll();
        if (sensors.isEmpty()) return;

        for (Sensor sensor : sensors) {
            double value;

            if (sensor.getType() == SensorType.CAMERA) {
                value = 1;
            } else {
                if (isCritical(sensor.getId())) {
                    value = simulateCriticalTemperature();
                } else {
                    value = simulateTemperature();
                }
            }

            sensor.setValue(value);
            sensor.setLastSeen(LocalDateTime.now());
            sensorRepository.save(sensor);

            if (sensor.getValue() != null && sensor.getValue() > CRITICAL_TEMPERATURE) {
                String sensorInfo = sensor.getName() + ": " + String.format("%.1f°C", sensor.getValue());
                notificationService.sendCriticalAlert(
                        sensor.getBuilding().getId(),
                        sensor.getBuilding().getName(),
                        sensorInfo
                );
            }
        }
    }

    private double simulateTemperature() {
        return BASE_TEMPERATURE + (random.nextDouble() * 2 - 1) * TEMPERATURE_VARIANCE;
    }

    private double simulateCriticalTemperature() {
        return CRITICAL_TEMPERATURE + random.nextDouble() * 30 + 0.1;
    }
}
