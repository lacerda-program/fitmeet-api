package bootcamp07.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateActivityRequestDTO {

    @NotBlank(message = "Informe os campos obrigatórios corretamente.")
    private String title;

    @NotBlank(message = "Informe os campos obrigatórios corretamente.")
    private String description;

    @NotNull(message = "Informe os campos obrigatórios corretamente.")
    private UUID typeId;

    @NotBlank(message = "Informe os campos obrigatórios corretamente.")
    private String address;

    @NotNull(message = "Informe os campos obrigatórios corretamente.")
    private LocalDateTime scheduledDate;

    @NotNull(message = "Informe os campos obrigatórios corretamente.")
    private Boolean isPrivate;
}