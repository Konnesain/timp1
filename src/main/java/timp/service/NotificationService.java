package timp.service;

import timp.dto.NotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void sendCriticalAlert(Long buildingId, String buildingName, String sensorInfo) {
        NotificationDto data = new NotificationDto(
                "CRITICAL_TEMPERATURE",
                buildingId,
                buildingName,
                sensorInfo
        );

        List<SseEmitter> dead = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("critical-alert")
                        .data(data));
            } catch (Exception e) {
                dead.add(emitter);
            }
        });
        emitters.removeAll(dead);
    }
}
