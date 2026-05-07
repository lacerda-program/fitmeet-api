package bootcamp07.api.controller;

import bootcamp07.api.dto.request.UpdateUserRequestDTO;
import bootcamp07.api.dto.response.PreferenceResponseDTO;
import bootcamp07.api.dto.response.UserResponseDTO;
import bootcamp07.api.model.User;
import bootcamp07.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints de gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Buscar dados do usuário")
    public ResponseEntity<UserResponseDTO> getUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUser(user));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Buscar interesses do usuário")
    public ResponseEntity<List<PreferenceResponseDTO>> getPreferences(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getPreferences(user));
    }

    @PostMapping("/preferences/define")
    @Operation(summary = "Definir interesses do usuário")
    public ResponseEntity<Map<String, String>> definePreferences(
            @AuthenticationPrincipal User user,
            @RequestBody List<UUID> typeIds) {
        userService.definePreferences(user, typeIds);
        return ResponseEntity.ok(Map.of("message", "Preferências atualizadas com sucesso."));
    }

    @PutMapping("/avatar")
    @Operation(summary = "Editar foto de perfil do usuário")
    public ResponseEntity<Map<String, String>> updateAvatar(
            @AuthenticationPrincipal User user,
            @RequestParam("avatar") MultipartFile file) {
        String avatarUrl = userService.updateAvatar(user, file);
        return ResponseEntity.ok(Map.of("avatar", avatarUrl));
    }

    @PutMapping("/update")
    @Operation(summary = "Editar dados do usuário")
    public ResponseEntity<UserResponseDTO> updateUser(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(user, dto));
    }

    @DeleteMapping("/deactivate")
    @Operation(summary = "Desativar conta do usuário")
    public ResponseEntity<Map<String, String>> deactivateAccount(
            @AuthenticationPrincipal User user) {
        userService.deactivateAccount(user);
        return ResponseEntity.ok(Map.of("message", "Conta desativada com sucesso."));
    }
}