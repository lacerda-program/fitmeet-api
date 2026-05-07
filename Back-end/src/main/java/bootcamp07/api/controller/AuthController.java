package bootcamp07.api.controller;

import bootcamp07.api.dto.request.LoginRequestDTO;
import bootcamp07.api.dto.request.RegisterRequestDTO;
import bootcamp07.api.dto.response.AuthResponseDTO;
import bootcamp07.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Cadastro de usuário")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuário criado com sucesso."));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Login de usuário")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}