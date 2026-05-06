package bootcamp07.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateActivityRequestDTO {

    private String title;

    private String description;

    private UUID typeId;

    private String address;

    private LocalDateTime scheduledDate;

    private Boolean isPrivate;
}