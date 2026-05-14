package timp.service;

import timp.dto.FireAccessRequest;
import timp.dto.FireAccessResponse;
import timp.model.FireAccess;
import timp.model.FireAccessBuilding;
import timp.repository.FireAccessBuildingRepository;
import timp.repository.FireAccessRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FireAccessService {

    private final FireAccessRepository fireAccessRepository;
    private final FireAccessBuildingRepository fireAccessBuildingRepository;

    public FireAccessService(FireAccessRepository fireAccessRepository, FireAccessBuildingRepository fireAccessBuildingRepository) {
        this.fireAccessRepository = fireAccessRepository;
        this.fireAccessBuildingRepository = fireAccessBuildingRepository;
    }

    @Transactional(readOnly = true)
    public List<FireAccessResponse> getAll() {
        return fireAccessRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FireAccessResponse create(FireAccessRequest request) {
        FireAccess fa = new FireAccess();
        fa.setPositionX(request.getPositionX());
        fa.setPositionY(request.getPositionY());
        fa.setWidth(request.getWidth());
        fa.setHeight(request.getHeight());
        fa.setAngle(request.getAngle());
        fa.setOpen(false);
        FireAccess saved = fireAccessRepository.save(fa);

        saveBuildingLinks(saved.getId(), request.getBuildingIds());

        return toResponse(saved);
    }

    public FireAccessResponse update(Long id, FireAccessRequest request) {
        FireAccess fa = fireAccessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пожарный подъезд не найден: " + id));
        fa.setPositionX(request.getPositionX());
        fa.setPositionY(request.getPositionY());
        fa.setWidth(request.getWidth());
        fa.setHeight(request.getHeight());
        fa.setAngle(request.getAngle());
        FireAccess saved = fireAccessRepository.save(fa);

        saveBuildingLinks(id, request.getBuildingIds());

        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!fireAccessRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пожарный подъезд не найден: " + id);
        }
        fireAccessBuildingRepository.deleteByFireAccessId(id);
        fireAccessRepository.deleteById(id);
    }

    private void saveBuildingLinks(Long fireAccessId, List<Long> buildingIds) {
        if (buildingIds == null || buildingIds.isEmpty()) return;

        List<FireAccessBuilding> currentLinks = fireAccessBuildingRepository.findByFireAccessId(fireAccessId);
        List<Long> currentBuildingIds = currentLinks.stream()
                .map(FireAccessBuilding::getBuildingId)
                .collect(Collectors.toList());

        List<Long> newBuildingIds = buildingIds;

        List<Long> toRemove = currentBuildingIds.stream()
                .filter(id -> !newBuildingIds.contains(id))
                .toList();

        List<Long> toAdd = newBuildingIds.stream()
                .filter(id -> !currentBuildingIds.contains(id))
                .toList();

        for (Long buildingId : toRemove) {
            fireAccessBuildingRepository.findOptByFireAccessIdAndBuildingId(fireAccessId, buildingId)
                    .ifPresent(fireAccessBuildingRepository::delete);
        }

        for (Long buildingId : toAdd) {
            FireAccessBuilding link = new FireAccessBuilding(fireAccessId, buildingId);
            fireAccessBuildingRepository.save(link);
        }
    }

    private FireAccessResponse toResponse(FireAccess fa) {
        List<Long> buildingIds = fireAccessBuildingRepository.findByFireAccessId(fa.getId())
                .stream()
                .map(FireAccessBuilding::getBuildingId)
                .collect(Collectors.toList());
        return new FireAccessResponse(
                fa.getId(),
                fa.getPositionX(),
                fa.getPositionY(),
                fa.getWidth(),
                fa.getHeight(),
                fa.getAngle(),
                fa.getOpen(),
                buildingIds
        );
    }
}