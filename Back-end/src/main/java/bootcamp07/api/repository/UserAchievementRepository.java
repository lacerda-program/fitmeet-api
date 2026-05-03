package bootcamp07.api.repository;

import bootcamp07.api.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {

    boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);
}
