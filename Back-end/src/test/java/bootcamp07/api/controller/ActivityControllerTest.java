package bootcamp07.api.controller;

import bootcamp07.api.dto.request.ApproveParticipantRequestDTO;
import bootcamp07.api.dto.request.CheckInRequestDTO;
import bootcamp07.api.dto.request.CreateActivityRequestDTO;
import bootcamp07.api.dto.response.ActivityPageResponseDTO;
import bootcamp07.api.dto.response.ActivityResponseDTO;
import bootcamp07.api.dto.response.ActivityTypeResponseDTO;
import bootcamp07.api.dto.response.ParticipantResponseDTO;
import bootcamp07.api.model.User;
import bootcamp07.api.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ActivityController activityController;

    private User user;
    private UUID activityId;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Samuel")
                .email("samuel@email.com")
                .xp(0)
                .level(1)
                .build();

        activityId = UUID.randomUUID();
    }

    @Test
    void getActivityTypes_Success() {
        List<ActivityTypeResponseDTO> types = List.of(
                ActivityTypeResponseDTO.builder()
                        .id(UUID.randomUUID())
                        .name("Futebol")
                        .build()
        );

        when(activityService.getActivityTypes()).thenReturn(types);

        ResponseEntity<List<ActivityTypeResponseDTO>> response =
                activityController.getActivityTypes(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Futebol", response.getBody().get(0).getName());
    }

    @Test
    void getActivities_Success() {
        ActivityPageResponseDTO pageResponse = ActivityPageResponseDTO.builder()
                .page(1)
                .pageSize(10)
                .totalActivities(0L)
                .totalPages(0)
                .activities(List.of())
                .build();

        when(activityService.getActivities(any(), anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(pageResponse);

        ResponseEntity<ActivityPageResponseDTO> response =
                activityController.getActivities(user, 1, 10, null, "createdAt", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createActivity_Success() {
        CreateActivityRequestDTO dto = new CreateActivityRequestDTO();
        dto.setTitle("Partida de Futebol");
        dto.setDescription("Futebol no parque");
        dto.setTypeId(UUID.randomUUID());
        dto.setScheduledDate(LocalDateTime.now().plusDays(1));
        dto.setIsPrivate(false);

        ActivityResponseDTO activityResponse = ActivityResponseDTO.builder()
                .id(UUID.randomUUID())
                .title("Partida de Futebol")
                .build();

        when(activityService.createActivity(any(), any(), any())).thenReturn(activityResponse);

        ResponseEntity<ActivityResponseDTO> response =
                activityController.createActivity(user, dto, null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Partida de Futebol", response.getBody().getTitle());
        verify(activityService, times(1)).createActivity(any(), any(), any());
    }

    @Test
    void subscribeToActivity_Success() {
        ActivityResponseDTO activityResponse = ActivityResponseDTO.builder()
                .id(activityId)
                .userSubscriptionStatus("APPROVED")
                .build();

        when(activityService.subscribeToActivity(any(), any())).thenReturn(activityResponse);

        ResponseEntity<ActivityResponseDTO> response =
                activityController.subscribeToActivity(user, activityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("APPROVED", response.getBody().getUserSubscriptionStatus());
    }

    @Test
    void concludeActivity_Success() {
        doNothing().when(activityService).concludeActivity(any(), any());

        ResponseEntity<Map<String, String>> response =
                activityController.concludeActivity(user, activityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Atividade concluída com sucesso.", response.getBody().get("message"));
    }

    @Test
    void deleteActivity_Success() {
        doNothing().when(activityService).deleteActivity(any(), any());

        ResponseEntity<Map<String, String>> response =
                activityController.deleteActivity(user, activityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Atividade excluída com sucesso.", response.getBody().get("message"));
    }

    @Test
    void checkIn_Success() {
        CheckInRequestDTO dto = new CheckInRequestDTO();
        dto.setConfirmationCode("ABC123");

        doNothing().when(activityService).checkIn(any(), any(), any());

        ResponseEntity<Map<String, String>> response =
                activityController.checkIn(user, activityId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Participação confirmada com sucesso.", response.getBody().get("message"));
    }

    @Test
    void unsubscribeFromActivity_Success() {
        doNothing().when(activityService).unsubscribeFromActivity(any(), any());

        ResponseEntity<Map<String, String>> response =
                activityController.unsubscribeFromActivity(user, activityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Participação cancelada com sucesso.", response.getBody().get("message"));
    }

    @Test
    void approveParticipant_Success() {
        ApproveParticipantRequestDTO dto = new ApproveParticipantRequestDTO();
        dto.setParticipantId(UUID.randomUUID());
        dto.setApproved(true);

        doNothing().when(activityService).approveParticipant(any(), any(), any());

        ResponseEntity<Map<String, String>> response =
                activityController.approveParticipant(user, activityId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Solicitação de participação aprovada com sucesso.",
                response.getBody().get("message"));
    }

    @Test
    void getParticipants_Success() {
        List<ParticipantResponseDTO> participants = List.of(
                ParticipantResponseDTO.builder()
                        .id(UUID.randomUUID())
                        .name("Samuel")
                        .build()
        );

        when(activityService.getParticipants(any(), any())).thenReturn(participants);

        ResponseEntity<List<ParticipantResponseDTO>> response =
                activityController.getParticipants(user, activityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}