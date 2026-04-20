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

    private final SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
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

    @Transactional
    public Optional<SensorResponse> receiveReading(Long sensorId, SensorValueRequest request) {
        Optional<Sensor> sensorOpt = sensorRepository.findById(sensorId);
        
        if (sensorOpt.isEmpty()) {
            return Optional.empty();
        }

        Sensor sensor = sensorOpt.get();
        sensor.setValue(request.getValue());
        sensor.setLastSeen(LocalDateTime.now());
        Sensor saved = sensorRepository.save(sensor);

        return Optional.of(SensorResponse.fromEntity(saved));
    }
}
