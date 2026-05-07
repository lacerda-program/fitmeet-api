package bootcamp07.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CheckInRequestDTO {

    @NotBlank(message = "Informe os campos obrigatórios corretamente.")
    private String confirmationCode;

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}