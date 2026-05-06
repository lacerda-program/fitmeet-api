package bootcamp07.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ActivityPageResponseDTO {

    private Integer page;
    private Integer pageSize;
    private Long totalActivities;
    private Integer totalPages;
    private Integer previous;
    private Integer next;
    private List<ActivityResponseDTO> activities;
}