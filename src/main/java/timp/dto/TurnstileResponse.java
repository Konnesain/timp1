package timp.dto;

public class TurnstileResponse {

    private final boolean granted;
    private final String message;

    public TurnstileResponse(boolean granted, String message) {
        this.granted = granted;
        this.message = message;
    }

    public boolean isGranted() { return granted; }
    public String getMessage() { return message; }
}