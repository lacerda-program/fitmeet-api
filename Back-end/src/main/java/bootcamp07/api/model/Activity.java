package bootcamp07.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ActivityType type;

    @Column(name = "confirmation_code", nullable = false)
    private String confirmationCode;

    private String image;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private Boolean private_ = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToOne(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ActivityAddress address;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActivityParticipant> participants;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.private_ == null) this.private_ = false;
    }
}