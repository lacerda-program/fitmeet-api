package bootcamp07.api.repository;

import bootcamp07.api.model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ActivityTypeRepository extends JpaRepository<ActivityType, UUID> {
}