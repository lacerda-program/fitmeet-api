package bootcamp07.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckInRequestDTO {
    @NotBlank(message = "Informe os campos obrigatórios corretamente.")
    private String comment;
}