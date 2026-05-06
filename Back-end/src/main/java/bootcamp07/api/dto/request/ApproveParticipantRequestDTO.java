package bootcamp07.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ApproveParticipantRequestDTO {

    @NotNull(message = "Informe os campos obrigatórios corretamente.")
    private UUID participantId;

    @NotNull(message = "Informe os campos obrigatórios corretamente.")
    private Boolean approved;
}