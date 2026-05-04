package bootcamp07.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDTO {

    private String name;

    private String email;

    private String password;
}