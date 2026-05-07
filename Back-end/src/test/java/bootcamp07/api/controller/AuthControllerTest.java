package bootcamp07.api.controller;

import bootcamp07.api.dto.request.LoginRequestDTO;
import bootcamp07.api.dto.request.RegisterRequestDTO;
import bootcamp07.api.dto.response.AuthResponseDTO;
import bootcamp07.api.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_Success() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setName("Samuel");
        dto.setEmail("samuel@email.com");
        dto.setCpf("123.456.789-00");
        dto.setPassword("senha123");

        doNothing().when(authService).register(any());

        ResponseEntity<Map<String, String>> response = authController.register(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Usuário criado com sucesso.", response.getBody().get("message"));
        verify(authService, times(1)).register(any());
    }

    @Test
    void login_Success() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("samuel@email.com");
        dto.setPassword("senha123");

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .token("jwt-token")
                .id(UUID.randomUUID())
                .name("Samuel")
                .email("samuel@email.com")
                .build();

        when(authService.login(any())).thenReturn(authResponse);

        ResponseEntity<AuthResponseDTO> response = authController.login(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().getToken());
        verify(authService, times(1)).login(any());
    }
}