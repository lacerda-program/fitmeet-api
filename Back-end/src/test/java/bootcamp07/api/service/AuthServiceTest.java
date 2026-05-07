package bootcamp07.api.service;

import bootcamp07.api.dto.request.LoginRequestDTO;
import bootcamp07.api.dto.request.RegisterRequestDTO;
import bootcamp07.api.dto.response.AuthResponseDTO;
import bootcamp07.api.exception.ConflictException;
import bootcamp07.api.exception.ForbiddenException;
import bootcamp07.api.exception.NotFoundException;
import bootcamp07.api.exception.UnauthorizedException;
import bootcamp07.api.model.User;
import bootcamp07.api.repository.UserRepository;
import bootcamp07.api.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;

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

        registerDTO = new RegisterRequestDTO();
        registerDTO.setName("Samuel");
        registerDTO.setEmail("samuel@email.com");
        registerDTO.setCpf("123.456.789-00");
        registerDTO.setPassword("senha123");

        loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("samuel@email.com");
        loginDTO.setPassword("senha123");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByCpf(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        assertDoesNotThrow(() -> authService.register(registerDTO));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void register_EmailAlreadyExists_ThrowsConflictException() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_CpfAlreadyExists_ThrowsConflictException() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByCpf(any())).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(loginDTO);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("samuel@email.com", response.getEmail());
    }

    @Test
    void login_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(loginDTO));
    }

    @Test
    void login_WrongPassword_ThrowsUnauthorizedException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginDTO));
    }

    @Test
    void login_DeactivatedAccount_ThrowsForbiddenException() {
        user.setDeletedAt(java.time.LocalDateTime.now());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class, () -> authService.login(loginDTO));
    }
}