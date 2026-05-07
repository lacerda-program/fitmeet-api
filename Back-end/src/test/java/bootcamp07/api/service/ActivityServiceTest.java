package bootcamp07.api.service;

import bootcamp07.api.dto.request.CreateActivityRequestDTO;
import bootcamp07.api.dto.response.ActivityResponseDTO;
import bootcamp07.api.exception.BusinessException;
import bootcamp07.api.exception.ConflictException;
import bootcamp07.api.exception.ForbiddenException;
import bootcamp07.api.exception.NotFoundException;
import bootcamp07.api.model.*;
import bootcamp07.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityTypeRepository activityTypeRepository;

    @Mock
    private ActivityParticipantRepository activityParticipantRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private AchievementService achievementService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ActivityService activityService;

    private User user;
    private User creator;
    private Activity activity;
    private ActivityType activityType;

    @BeforeEach
    void setUp() {
        activityType = ActivityType.builder()
                .id(UUID.randomUUID())
                .name("Futebol")
                .build();

        creator = User.builder()
                .id(UUID.randomUUID())
                .name("Criador")
                .email("criador@email.com")
                .xp(0)
                .level(1)
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .name("Samuel")
                .email("samuel@email.com")
                .xp(0)
                .level(1)
                .build();

        activity = Activity.builder()
                .id(UUID.randomUUID())
                .title("Partida de Futebol")
                .description("Futebol no parque")
                .type(activityType)
                .confirmationCode("ABC123")
                .private_(false)
                .creator(creator)
                .scheduledDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void createActivity_Success() {
        CreateActivityRequestDTO dto = new CreateActivityRequestDTO();
        dto.setTitle("Partida de Futebol");
        dto.setDescription("Futebol no parque");
        dto.setTypeId(activityType.getId());
        dto.setScheduledDate(LocalDateTime.now().plusDays(1));
        dto.setIsPrivate(false);

        when(activityTypeRepository.findById(any())).thenReturn(Optional.of(activityType));
        when(activityRepository.save(any())).thenReturn(activity);
        when(activityParticipantRepository.findByActivityId(any())).thenReturn(java.util.List.of());

        ActivityResponseDTO response = activityService.createActivity(creator, dto, null);

        assertNotNull(response);
        verify(activityRepository, times(1)).save(any());
        verify(achievementService, times(1)).grantFirstActivity(any());
    }

    @Test
    void createActivity_DeactivatedAccount_ThrowsForbiddenException() {
        creator.setDeletedAt(LocalDateTime.now());
        CreateActivityRequestDTO dto = new CreateActivityRequestDTO();
        dto.setTypeId(activityType.getId());

        assertThrows(ForbiddenException.class,
                () -> activityService.createActivity(creator, dto, null));
    }

    @Test
    void subscribeToActivity_Success() {
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));
        when(activityParticipantRepository.existsByActivityIdAndUserId(any(), any()))
                .thenReturn(false);
        when(activityParticipantRepository.save(any())).thenReturn(
                ActivityParticipant.builder()
                        .id(UUID.randomUUID())
                        .activity(activity)
                        .user(user)
                        .approved(true)
                        .build());

        ActivityResponseDTO response = activityService.subscribeToActivity(user, activity.getId());

        assertNotNull(response);
        verify(activityParticipantRepository, times(1)).save(any());
    }

    @Test
    void subscribeToActivity_AlreadySubscribed_ThrowsConflictException() {
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));
        when(activityParticipantRepository.existsByActivityIdAndUserId(any(), any()))
                .thenReturn(true);

        assertThrows(ConflictException.class,
                () -> activityService.subscribeToActivity(user, activity.getId()));
    }

    @Test
    void subscribeToActivity_CreatorCannotSubscribe_ThrowsBusinessException() {
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));

        assertThrows(BusinessException.class,
                () -> activityService.subscribeToActivity(creator, activity.getId()));
    }

    @Test
    void subscribeToActivity_CompletedActivity_ThrowsBusinessException() {
        activity.setCompletedAt(LocalDateTime.now());
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));

        assertThrows(BusinessException.class,
                () -> activityService.subscribeToActivity(user, activity.getId()));
    }

    @Test
    void concludeActivity_Success() {
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));
        when(activityRepository.save(any())).thenReturn(activity);

        assertDoesNotThrow(() -> activityService.concludeActivity(creator, activity.getId()));
        assertNotNull(activity.getCompletedAt());
        verify(achievementService, times(1)).grantFirstComplete(any());
    }

    @Test
    void concludeActivity_NotCreator_ThrowsForbiddenException() {
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));

        assertThrows(ForbiddenException.class,
                () -> activityService.concludeActivity(user, activity.getId()));
    }

    @Test
    void deleteActivity_Success() {
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));
        when(activityRepository.save(any())).thenReturn(activity);

        assertDoesNotThrow(() -> activityService.deleteActivity(creator, activity.getId()));
        assertNotNull(activity.getDeletedAt());
    }

    @Test
    void deleteActivity_NotCreator_ThrowsForbiddenException() {
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));

        assertThrows(ForbiddenException.class,
                () -> activityService.deleteActivity(user, activity.getId()));
    }

    @Test
    void checkIn_ActivityNotFound_ThrowsNotFoundException() {
        when(activityRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> activityService.checkIn(user, UUID.randomUUID(), "ABC123"));
    }

    @Test
    void unsubscribeFromActivity_AlreadyConfirmed_ThrowsBusinessException() {
        ActivityParticipant participant = ActivityParticipant.builder()
                .id(UUID.randomUUID())
                .activity(activity)
                .user(user)
                .approved(true)
                .confirmedAt(LocalDateTime.now())
                .build();

        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));
        when(activityParticipantRepository.findByActivityIdAndUserId(any(), any()))
                .thenReturn(Optional.of(participant));

        assertThrows(BusinessException.class,
                () -> activityService.unsubscribeFromActivity(user, activity.getId()));
    }
}