package timp.controller;

import timp.dto.BuildingDetailsResponse;
import timp.dto.BuildingRequest;
import timp.dto.BuildingResponse;
import timp.service.BuildingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<List<BuildingResponse>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingDetailsResponse> getBuildingById(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.getBuildingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponse> updateBuilding(@PathVariable Long id,
                                                            @Valid @RequestBody BuildingRequest request) {
        return ResponseEntity.ok(buildingService.updateBuilding(id, request));
    }

    @PostMapping
    public ResponseEntity<BuildingResponse> createBuilding(@Valid @RequestBody BuildingRequest request) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(buildingService.createBuilding(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}