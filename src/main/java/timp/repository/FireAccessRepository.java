package timp.repository;

import timp.model.FireAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireAccessRepository extends JpaRepository<FireAccess, Long> {
}
