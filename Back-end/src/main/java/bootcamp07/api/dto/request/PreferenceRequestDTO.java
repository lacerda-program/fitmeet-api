package bootcamp07.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PreferenceRequestDTO {

    @NotNull(message = "Informe os campos obrigatórios corretamente.")
    private List<UUID> typeIds;
}