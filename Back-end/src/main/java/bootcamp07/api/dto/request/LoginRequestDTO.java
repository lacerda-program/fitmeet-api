package bootcamp07.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Informe os campos obrigatórios corretamente.")
    @Email(message = "Informe os campos obrigatórios corretamente.")
    private String email;

    @NotBlank(message = "Informe os campos obrigatórios corretamente.")
    private String password;
}