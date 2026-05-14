package timp.repository;

import timp.model.FireAccessBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FireAccessBuildingRepository extends JpaRepository<FireAccessBuilding, Long> {
    List<FireAccessBuilding> findByFireAccessId(Long fireAccessId);
    List<FireAccessBuilding> findByBuildingId(Long buildingId);
    void deleteByFireAccessId(Long fireAccessId);
    boolean existsByFireAccessIdAndBuildingId(Long fireAccessId, Long buildingId);
    FireAccessBuilding findByFireAccessIdAndBuildingId(Long fireAccessId, Long buildingId);
    Optional<FireAccessBuilding> findOptByFireAccessIdAndBuildingId(Long fireAccessId, Long buildingId);
}