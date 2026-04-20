package timp.service;

import timp.model.SecurityEvent;
import timp.repository.SecurityEventRepository;
import timp.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SecurityEventLogger {

    private final SecurityEventRepository securityEventRepository;
    private final SecurityUtil securityUtil;

    public SecurityEventLogger(SecurityEventRepository securityEventRepository, SecurityUtil securityUtil) {
        this.securityEventRepository = securityEventRepository;
        this.securityUtil = securityUtil;
    }

    public void log(SecurityEvent.EventType type, String details, boolean success) {
        SecurityEvent event = new SecurityEvent(
                securityUtil.getCurrentUserId(),
                securityUtil.getCurrentUsername(),
                type,
                details,
                LocalDateTime.now(),
                success
        );
        securityEventRepository.save(event);
    }

    public void log(SecurityEvent.EventType type, String details, boolean success, int userId, String userName) {
        SecurityEvent event = new SecurityEvent(
                userId,
                userName,
                type,
                details,
                LocalDateTime.now(),
                success
        );
        securityEventRepository.save(event);
    }

    public void logBuildingView(String buildingName) {
        log(SecurityEvent.EventType.BUILDING_VIEW, "Просмотр здания: " + buildingName, true);
    }

    public void logBuildingEdit(String buildingName) {
        log(SecurityEvent.EventType.BUILDING_EDIT, "Редактирование здания: " + buildingName, true);
    }
    
    public void logEmployeeRequest() {
        log(SecurityEvent.EventType.EMPLOYEE_REQUEST, "Запрос списка сотрудников", true);
    }

    public void logEmployeeView(Long employeeId, String employeeName) {
        log(SecurityEvent.EventType.EMPLOYEE_REQUEST, "Просмотр сотрудника: " + employeeName + " (ID: " + employeeId + ")", true);
    }

    public void logEmployeeCreate(String employeeName, Long employeeId, String position) {
        log(SecurityEvent.EventType.EMPLOYEE_CREATE,
            "Создан сотрудник: " + employeeName + " (ID: " + employeeId + "), должность: " + position, true);
    }

    public void logEmployeeEdit(Long employeeId, String oldInfo, String newInfo) {
        log(SecurityEvent.EventType.EMPLOYEE_EDIT, "Обновление: " + oldInfo + " → " + newInfo + " (ID: " + employeeId + ")", true);
    }

    public void logEmployeeDelete(Long employeeId, String employeeName) {
        log(SecurityEvent.EventType.EMPLOYEE_DELETE, "Удалён сотрудник: " + employeeName + " (ID: " + employeeId + ")", true);
    }

    public void logAccessAttempt(String buildingName, String action, boolean success, int userId, String userName) {
        log(SecurityEvent.EventType.ACCESS_ATTEMPT, buildingName + " | Действие: " + action, success, userId, userName);
    }

    public void logAccessAttempt(String buildingName, String action, boolean success) {
        log(SecurityEvent.EventType.ACCESS_ATTEMPT, buildingName + " | Действие: " + action, success);
    }

    public void logAccessAttemptUnknown(String details, boolean success) {
        log(SecurityEvent.EventType.ACCESS_ATTEMPT, details, success, 0, "Неизвестный");
    }

    public void logAuthLogin(String username) {
        log(SecurityEvent.EventType.AUTH_LOGIN, "Успешный вход: " + username, true, 0, username);
    }

    public void logAuthFailed(String username) {
        log(SecurityEvent.EventType.AUTH_FAILED, "Неверный логин или пароль: " + username, false, 0, username);
    }
}