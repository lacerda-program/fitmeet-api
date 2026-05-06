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
public class PreferenceResponseDTO {

    private UUID typeId;
    private String typeName;
    private String typeDescription;
}