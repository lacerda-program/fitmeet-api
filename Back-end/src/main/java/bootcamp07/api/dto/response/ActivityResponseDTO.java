package bootcamp07.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ActivityResponseDTO {

    private UUID id;
    private String title;
    private String description;
    private String type;
    private String image;
    private String confirmationCode;
    private Integer participantCount;
    private AddressResponseDTO address;
    private LocalDateTime scheduledDate;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Boolean private_;
    private CreatorResponseDTO creator;
    private String userSubscriptionStatus;
}