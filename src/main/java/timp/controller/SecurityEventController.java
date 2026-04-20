package timp.controller;

import timp.dto.EventTypeResponse;
import timp.dto.SecurityEventResponse;
import timp.model.SecurityEvent;
import timp.service.SecurityEventService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class SecurityEventController {

    private final SecurityEventService securityEventService;

    public SecurityEventController(SecurityEventService securityEventService) {
        this.securityEventService = securityEventService;
    }

    @GetMapping
    public ResponseEntity<Page<SecurityEventResponse>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String type) {

        Page<SecurityEventResponse> events = securityEventService.getEvents(page, size, type);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/types")
    public ResponseEntity<EventTypeResponse> getEventTypes() {
        Map<String, String> types = new LinkedHashMap<>();
        for (SecurityEvent.EventType type : SecurityEvent.EventType.values()) {
            types.put(type.name(), type.toString());
        }
        return ResponseEntity.ok(new EventTypeResponse(types));
    }
}
