package timp.service;

import timp.dto.SensorValueRequest;
import timp.model.Sensor;
import timp.model.Sensor.SensorType;
import timp.repository.SensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SensorSimulator {

    private static final Logger log = LoggerFactory.getLogger(SensorSimulator.class);
    private static final double BASE_TEMPERATURE = 22.0;
    private static final double TEMPERATURE_VARIANCE = 2.0;
    private static final double CRITICAL_TEMPERATURE = 50.0;

    private final SensorService sensorService;
    private final SensorRepository sensorRepository;
    private final Random random = new Random();
    private final Set<Long> criticalSensorIds = ConcurrentHashMap.newKeySet();

    public SensorSimulator(SensorService sensorService, SensorRepository sensorRepository) {
        this.sensorService = sensorService;
        this.sensorRepository = sensorRepository;
    }

    public void igniteBuilding(Long buildingId) {
        List<Sensor> sensors = sensorRepository.findByBuildingId(buildingId).stream()
                .filter(s -> s.getType() == SensorType.TEMPERATURE)
                .toList();
        sensors.forEach(s -> criticalSensorIds.add(s.getId()));
        log.info("Ignited {} temperature sensors", criticalSensorIds.size());
    }

    public void extinguishBuilding(Long buildingId) {
        List<Sensor> sensors = sensorRepository.findByBuildingId(buildingId).stream()
                .filter(s -> s.getType() == SensorType.TEMPERATURE)
                .toList();
        sensors.forEach(s -> criticalSensorIds.remove(s.getId()));
        log.info("Extinguished building {}", buildingId);
    }

    public boolean isCritical(Long sensorId) {
        return criticalSensorIds.contains(sensorId);
    }

    @Scheduled(fixedRate = 5000)
    public void simulateReadings() {
        List<Sensor> sensors = sensorRepository.findAll();
        if (sensors.isEmpty()) return;

        for (Sensor sensor : sensors) {
            double value = isCritical(sensor.getId())
                    ? simulateCriticalTemperature()
                    : simulateTemperature();

            sensorService.receiveReading(sensor.getId(), new SensorValueRequest(value));
        }
    }

    private double simulateTemperature() {
        return BASE_TEMPERATURE + (random.nextDouble() * 2 - 1) * TEMPERATURE_VARIANCE;
    }

    private double simulateCriticalTemperature() {
        return CRITICAL_TEMPERATURE + random.nextDouble() * 30 + 0.1;
    }
}
