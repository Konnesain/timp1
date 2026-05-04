package timp.service;

import timp.dto.NotificationDto;
import timp.model.User;
import timp.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();
    private final Set<Long> alertedBuildings = ConcurrentHashMap.newKeySet();

    public NotificationService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void sendCriticalAlert(Long buildingId, String buildingName, String sensorInfo) {
        if(shouldSendAlert(buildingId))
        {
            sendSseAlert(buildingId, buildingName, sensorInfo);
            sendEmailAlert(buildingId, buildingName, sensorInfo);
        }
    }

    private boolean shouldSendAlert(Long buildingId) {
        if (alertedBuildings.contains(buildingId)) {
            return false;
        }
        alertedBuildings.add(buildingId);
        return true;
    }

    private void sendSseAlert(Long buildingId, String buildingName, String sensorInfo) {
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

    private void sendEmailAlert(Long buildingId, String buildingName, String sensorInfo) {
        List<String> emails = userRepository.findAll().stream()
                .map(User::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .toList();

        if (emails.isEmpty()) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emails.toArray(new String[0]));
            message.setSubject("Пожарная тревога в " + buildingName);
            message.setText("Обнаружена критическая температура!\nЗдание: " + buildingName + "\nSensor Info: " + sensorInfo);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    public void resetAlertIfResolved(Long buildingId, boolean isCritical) {
        if (!isCritical) {
            alertedBuildings.remove(buildingId);
        }
    }
}
