package timp.service;

import timp.dto.SensorValueRequest;
import timp.dto.SensorResponse;
import timp.model.Building;
import timp.model.Sensor;
import timp.model.FireAccessBuilding;
import timp.repository.BuildingRepository;
import timp.repository.FireAccessBuildingRepository;
import timp.repository.FireAccessRepository;
import timp.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SensorService {

    private static final double CRITICAL_TEMPERATURE = 50.0;

    private final SensorRepository sensorRepository;
    private final NotificationService notificationService;
    private final FireAccessRepository fireAccessRepository;
    private final FireAccessBuildingRepository fireAccessBuildingRepository;
    private final BuildingRepository buildingRepository;
    private final BuildingService buildingService;

    public SensorService(SensorRepository sensorRepository, NotificationService notificationService,
                         FireAccessRepository fireAccessRepository, FireAccessBuildingRepository fireAccessBuildingRepository,
                         BuildingRepository buildingRepository, BuildingService buildingService) {
        this.sensorRepository = sensorRepository;
        this.notificationService = notificationService;
        this.fireAccessRepository = fireAccessRepository;
        this.fireAccessBuildingRepository = fireAccessBuildingRepository;
        this.buildingRepository = buildingRepository;
        this.buildingService = buildingService;
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
        sensor.setLastSeen(java.time.LocalDateTime.now());
        Sensor saved = sensorRepository.save(sensor);

        List<Sensor> buildingSensors = sensorRepository.findByBuildingId(sensor.getBuilding().getId());
        boolean hasCritical = hasCriticalSensors(buildingSensors);
        boolean hasOffline = buildingSensors.stream().anyMatch(s -> !s.isOnline());

        Building.Status newStatus;
        if (hasCritical) {
            newStatus = Building.Status.CRITICAL;
        } else if (hasOffline) {
            newStatus = Building.Status.WARNING;
        } else {
            newStatus = Building.Status.OK;
        }

        buildingService.updateStatus(sensor.getBuilding().getId(), newStatus);

        if (saved.getValue() != null && saved.getValue() > CRITICAL_TEMPERATURE) {
            String sensorInfo = sensor.getName() + ": " + String.format("%.1f°C", sensor.getValue());
            notificationService.sendCriticalAlert(sensor.getBuilding().getId(), sensor.getBuilding().getName(), sensorInfo);
            openFireAccessesForBuilding(sensor.getBuilding().getId());
        } else if (newStatus == Building.Status.OK) {
            closeFireAccessesForBuilding(sensor.getBuilding().getId());
            notificationService.resetAlert(sensor.getBuilding().getId());
        }
    }

    private void openFireAccessesForBuilding(Long buildingId) {
        List<FireAccessBuilding> links = fireAccessBuildingRepository.findByBuildingId(buildingId);
        for (FireAccessBuilding link : links) {
            fireAccessRepository.findById(link.getFireAccessId()).ifPresent(fa -> {
                fa.setOpen(true);
                fireAccessRepository.save(fa);
            });
        }
    }

    private void closeFireAccessesForBuilding(Long buildingId) {
        List<Building> criticalBuildings = buildingRepository.findByStatus(Building.Status.CRITICAL);
        List<Long> criticalBuildingIds = criticalBuildings.stream().map(Building::getId).toList();

        List<FireAccessBuilding> links = fireAccessBuildingRepository.findByBuildingId(buildingId);
        for (FireAccessBuilding link : links) {
            fireAccessRepository.findById(link.getFireAccessId()).ifPresent(fa -> {
                boolean stillNeededByOthers = fireAccessBuildingRepository.findByFireAccessId(fa.getId())
                        .stream()
                        .anyMatch(l -> l.getBuildingId() != buildingId && criticalBuildingIds.contains(l.getBuildingId()));

                if (!stillNeededByOthers) {
                    fa.setOpen(false);
                    fireAccessRepository.save(fa);
                }
            });
        }
    }
}
