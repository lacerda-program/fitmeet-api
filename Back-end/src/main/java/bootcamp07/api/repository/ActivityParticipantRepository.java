package bootcamp07.api.repository;

import bootcamp07.api.model.ActivityParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, UUID> {

    boolean existsByActivityIdAndUserId(UUID activityId, UUID userId);

    Optional<ActivityParticipant> findByActivityIdAndUserId(UUID activityId, UUID userId);

    List<ActivityParticipant> findByActivityId(UUID activityId);

    @Query("SELECT ap FROM ActivityParticipant ap WHERE ap.user.id = :userId AND ap.activity.deletedAt IS NULL")
    Page<ActivityParticipant> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT ap FROM ActivityParticipant ap WHERE ap.user.id = :userId AND ap.activity.deletedAt IS NULL")
    List<ActivityParticipant> findAllByUserId(@Param("userId") UUID userId);
}