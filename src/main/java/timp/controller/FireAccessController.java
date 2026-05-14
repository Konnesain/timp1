package timp.controller;

import timp.dto.FireAccessRequest;
import timp.dto.FireAccessResponse;
import timp.service.FireAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fire-access")
public class FireAccessController {

    private final FireAccessService fireAccessService;

    public FireAccessController(FireAccessService fireAccessService) {
        this.fireAccessService = fireAccessService;
    }

    @GetMapping
    public ResponseEntity<List<FireAccessResponse>> getAll() {
        return ResponseEntity.ok(fireAccessService.getAll());
    }

    @PostMapping
    public ResponseEntity<FireAccessResponse> create(@RequestBody FireAccessRequest request) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(fireAccessService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FireAccessResponse> update(@PathVariable Long id,
                                                     @RequestBody FireAccessRequest request) {
        return ResponseEntity.ok(fireAccessService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fireAccessService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
