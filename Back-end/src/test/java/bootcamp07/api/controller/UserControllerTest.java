package bootcamp07.api.controller;

import bootcamp07.api.dto.request.UpdateUserRequestDTO;
import bootcamp07.api.dto.response.PreferenceResponseDTO;
import bootcamp07.api.dto.response.UserResponseDTO;
import bootcamp07.api.model.User;
import bootcamp07.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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
        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .id(user.getId())
                .name("Samuel")
                .email("samuel@email.com")
                .build();

        when(userService.getUser(any())).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.getUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Samuel", response.getBody().getName());
        verify(userService, times(1)).getUser(any());
    }

    @Test
    void updateUser_Success() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setName("Samuel Atualizado");

        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .id(user.getId())
                .name("Samuel Atualizado")
                .email("samuel@email.com")
                .build();

        when(userService.updateUser(any(), any())).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.updateUser(user, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Samuel Atualizado", response.getBody().getName());
    }

    @Test
    void getPreferences_Success() {
        List<PreferenceResponseDTO> preferences = List.of(
                PreferenceResponseDTO.builder()
                        .typeId(UUID.randomUUID())
                        .typeName("Futebol")
                        .build()
        );

        when(userService.getPreferences(any())).thenReturn(preferences);

        ResponseEntity<List<PreferenceResponseDTO>> response = userController.getPreferences(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void deactivateAccount_Success() {
        doNothing().when(userService).deactivateAccount(any());

        ResponseEntity<Map<String, String>> response = userController.deactivateAccount(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Conta desativada com sucesso.", response.getBody().get("message"));
        verify(userService, times(1)).deactivateAccount(any());
    }
}