package timp.repository;

import timp.model.SecurityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
    
    Page<SecurityEvent> findAllByOrderByTimestampDesc(Pageable pageable);
    
    Page<SecurityEvent> findByTypeInOrderByTimestampDesc(List<SecurityEvent.EventType> types, Pageable pageable);
}
