package bootcamp07.api.service;

import bootcamp07.api.dto.request.UpdateUserRequestDTO;
import bootcamp07.api.dto.response.UserResponseDTO;
import bootcamp07.api.exception.ConflictException;
import bootcamp07.api.exception.ForbiddenException;
import bootcamp07.api.model.User;
import bootcamp07.api.repository.ActivityTypeRepository;
import bootcamp07.api.repository.PreferenceRepository;
import bootcamp07.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private ActivityTypeRepository activityTypeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StorageService storageService;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Samuel")
                .email("samuel@email.com")
                .cpf("123.456.789-00")
                .password("encodedPassword")
                .xp(0)
                .level(1)
                .build();
    }

    @Test
    void getUser_Success() {
        UserResponseDTO response = userService.getUser(user);

        assertNotNull(response);
        assertEquals("samuel@email.com", response.getEmail());
        assertEquals("Samuel", response.getName());
    }

    @Test
    void getUser_DeactivatedAccount_ThrowsForbiddenException() {
        user.setDeletedAt(LocalDateTime.now());

        assertThrows(ForbiddenException.class, () -> userService.getUser(user));
    }

    @Test
    void updateUser_Success() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setName("Samuel Atualizado");

        when(userRepository.save(any())).thenReturn(user);

        UserResponseDTO response = userService.updateUser(user, dto);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser_EmailAlreadyExists_ThrowsConflictException() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("outro@email.com");

        when(userRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateUser(user, dto));
    }

    @Test
    void updateUser_DeactivatedAccount_ThrowsForbiddenException() {
        user.setDeletedAt(LocalDateTime.now());
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();

        assertThrows(ForbiddenException.class, () -> userService.updateUser(user, dto));
    }

    @Test
    void deactivateAccount_Success() {
        when(userRepository.save(any())).thenReturn(user);

        assertDoesNotThrow(() -> userService.deactivateAccount(user));
        assertNotNull(user.getDeletedAt());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deactivateAccount_AlreadyDeactivated_ThrowsForbiddenException() {
        user.setDeletedAt(LocalDateTime.now());

        assertThrows(ForbiddenException.class, () -> userService.deactivateAccount(user));
    }

    @Test
    void addXp_LevelUp_Success() {
        user.setXp(90);
        user.setLevel(1);

        when(userRepository.save(any())).thenReturn(user);

        userService.addXp(user, 50);

        assertEquals(140, user.getXp());
        assertEquals(2, user.getLevel());
        verify(achievementService, times(1)).grantFirstLevelUp(any());
    }
}