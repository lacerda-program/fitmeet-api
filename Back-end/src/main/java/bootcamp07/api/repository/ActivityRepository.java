package bootcamp07.api.repository;

import bootcamp07.api.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL AND a.completedAt IS NULL AND (:typeId IS NULL OR a.type.id = :typeId)")
    Page<Activity> findAvailableActivities(@Param("typeId") UUID typeId, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL AND a.completedAt IS NULL AND (:typeId IS NULL OR a.type.id = :typeId)")
    List<Activity> findAllAvailableActivities(@Param("typeId") UUID typeId);

    @Query("SELECT a FROM Activity a WHERE a.creator.id = :creatorId AND a.deletedAt IS NULL")
    Page<Activity> findByCreatorId(@Param("creatorId") UUID creatorId, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.creator.id = :creatorId AND a.deletedAt IS NULL")
    List<Activity> findAllByCreatorId(@Param("creatorId") UUID creatorId);
}