package timp.dto;

import java.util.Map;

public class EventTypeResponse {

    private Map<String, String> types;

    public EventTypeResponse(Map<String, String> types) { this.types = types; }

    public Map<String, String> getTypes() { return types; }
    public void setTypes(Map<String, String> types) { this.types = types; }
}