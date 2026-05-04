package timp.service;

import timp.dto.RoadRequest;
import timp.dto.RoadResponse;
import timp.model.Road;
import timp.repository.RoadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoadService {

    private final RoadRepository roadRepository;

    public RoadService(RoadRepository roadRepository) {
        this.roadRepository = roadRepository;
    }

    @Transactional(readOnly = true)
    public List<RoadResponse> getAllRoads() {
        return roadRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoadResponse createRoad(RoadRequest request) {
        Road road = new Road();
        road.setName(request.getName());
        road.setPositionX(request.getPositionX());
        road.setPositionY(request.getPositionY());
        road.setWidth(request.getWidth());
        road.setHeight(request.getHeight());
        road.setAngle(request.getAngle());
        road.setDescription(request.getDescription());
        Road saved = roadRepository.save(road);
        return toResponse(saved);
    }

    public RoadResponse updateRoad(Long id, RoadRequest request) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Дорога не найдена: " + id));
        road.setName(request.getName());
        road.setPositionX(request.getPositionX());
        road.setPositionY(request.getPositionY());
        road.setWidth(request.getWidth());
        road.setHeight(request.getHeight());
        road.setAngle(request.getAngle());
        road.setDescription(request.getDescription());
        Road saved = roadRepository.save(road);
        return toResponse(saved);
    }

    public void deleteRoad(Long id) {
        if (!roadRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Дорога не найдена: " + id);
        }
        roadRepository.deleteById(id);
    }

    private RoadResponse toResponse(Road road) {
        return new RoadResponse(
                road.getId(),
                road.getName(),
                road.getPositionX(),
                road.getPositionY(),
                road.getWidth(),
                road.getHeight(),
                road.getAngle(),
                road.getDescription()
        );
    }
}
