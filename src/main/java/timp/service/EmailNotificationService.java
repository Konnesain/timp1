package timp.service;

import timp.model.User;
import timp.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public EmailNotificationService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @Async
    public void sendEmailAlert(Long buildingId, String buildingName, String sensorInfo) {
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
}
