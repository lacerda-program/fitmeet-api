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
public class ParticipantResponseDTO {

    private UUID id;
    private UUID userId;
    private String name;
    private String avatar;
    private String subscriptionStatus;
    private LocalDateTime confirmedAt;
}