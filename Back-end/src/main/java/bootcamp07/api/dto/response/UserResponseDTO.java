package bootcamp07.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String avatar;
    private Integer xp;
    private Integer level;
    private List<AchievementResponseDTO> achievements;
}