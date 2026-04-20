package timp.service;

import timp.dto.SecurityEventResponse;
import timp.model.SecurityEvent;
import timp.repository.SecurityEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SecurityEventService {
    
    private final SecurityEventRepository securityEventRepository;
    
    public SecurityEventService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }
    
    @Transactional(readOnly = true)
    public Page<SecurityEventResponse> getEvents(int page, int size, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        
        if (type != null && !type.isEmpty() && !type.equals("ALL")) {
            List<SecurityEvent.EventType> types = Arrays.stream(type.split(","))
                    .map(String::trim)
                    .map(SecurityEvent.EventType::valueOf)
                    .collect(Collectors.toList());
            return securityEventRepository.findByTypeInOrderByTimestampDesc(types, pageable)
                    .map(this::toResponse);
        }
        
        return securityEventRepository.findAllByOrderByTimestampDesc(pageable)
                .map(this::toResponse);
    }
    
    private SecurityEventResponse toResponse(SecurityEvent event) {
        return new SecurityEventResponse(
                event.getId(),
                event.getUserId(),
                event.getUserName(),
                event.getType().name(),
                event.getDetails(),
                event.getTimestamp(),
                event.isSuccess()
        );
    }
}
