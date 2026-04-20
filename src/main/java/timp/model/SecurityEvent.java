package timp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_events")
public class SecurityEvent {

    public enum EventType {
        ACCESS_ATTEMPT("Попытка прохода через турникет"),
        EMPLOYEE_EDIT("Изменение сотрудника"),
        EMPLOYEE_REQUEST("Запрос списка сотрудников"),
        BUILDING_VIEW("Просмотр здания"),
        BUILDING_EDIT("Редактирование здания"),
        EMPLOYEE_CREATE("Создание сотрудника"),
        EMPLOYEE_DELETE("Удаление сотрудника"),
        AUTH_LOGIN("Вход в систему"),
        AUTH_FAILED("Неудачная попытка входа");

        private final String label;
        EventType(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int userId;
    private String userName;

    @Enumerated(EnumType.STRING)
    private EventType type;

    private String details;

    private LocalDateTime timestamp;

    private boolean success;

    public SecurityEvent() { }

    public SecurityEvent(int userId, String userName, EventType type, String details, LocalDateTime timestamp, boolean success) {
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.details = details;
        this.timestamp = timestamp;
        this.success = success;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}