package timp.dto;

import java.time.LocalDateTime;

public class SecurityEventResponse {

    private final Long id;
    private final int userId;
    private final String userName;
    private final String type;
    private final String details;
    private final LocalDateTime timestamp;
    private final boolean success;

    public SecurityEventResponse(Long id, int userId, String userName, String type,
                                  String details, LocalDateTime timestamp, boolean success) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.details = details;
        this.timestamp = timestamp;
        this.success = success;
    }

    public Long getId() { return id; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getType() { return type; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isSuccess() { return success; }
}