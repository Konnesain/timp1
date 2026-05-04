package timp.controller;

import timp.dto.RoadRequest;
import timp.dto.RoadResponse;
import timp.service.RoadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roads")
public class RoadController {

    private final RoadService roadService;

    public RoadController(RoadService roadService) {
        this.roadService = roadService;
    }

    @GetMapping
    public ResponseEntity<List<RoadResponse>> getAllRoads() {
        return ResponseEntity.ok(roadService.getAllRoads());
    }

    @PostMapping
    public ResponseEntity<RoadResponse> createRoad(@Valid @RequestBody RoadRequest request) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(roadService.createRoad(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoadResponse> updateRoad(@PathVariable Long id,
                                                    @Valid @RequestBody RoadRequest request) {
        return ResponseEntity.ok(roadService.updateRoad(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoad(@PathVariable Long id) {
        roadService.deleteRoad(id);
        return ResponseEntity.noContent().build();
    }
}
