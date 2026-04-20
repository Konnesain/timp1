package timp.controller;

import timp.dto.TurnstileResponse;
import timp.service.TurnstileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/turnstile")
public class TurnstileController {

    private final TurnstileService turnstileService;

    public TurnstileController(TurnstileService turnstileService) {
        this.turnstileService = turnstileService;
    }

    @PostMapping("/{employeeId}/{buildingId}/{action}")
    public ResponseEntity<TurnstileResponse> processAccess(
            @PathVariable Long employeeId,
            @PathVariable Long buildingId,
            @PathVariable String action) {

        TurnstileResponse response = turnstileService.processAccess(employeeId, buildingId, action);
        return ResponseEntity.ok(response);
    }
}