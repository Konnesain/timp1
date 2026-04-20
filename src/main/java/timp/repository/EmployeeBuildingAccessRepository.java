package timp.repository;

import timp.model.EmployeeBuildingAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeBuildingAccessRepository extends JpaRepository<EmployeeBuildingAccess, Long> {

    List<EmployeeBuildingAccess> findByEmployeeId(Long employeeId);

    Optional<EmployeeBuildingAccess> findByEmployeeIdAndBuildingId(Long employeeId, Long buildingId);

    List<EmployeeBuildingAccess> findByBuildingId(Long buildingId);

    void deleteByEmployeeId(Long employeeId);

    boolean existsByEmployeeIdAndBuildingId(Long employeeId, Long buildingId);
}
