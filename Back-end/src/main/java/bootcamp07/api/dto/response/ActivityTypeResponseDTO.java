package bootcamp07.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ActivityTypeResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private String image;
}