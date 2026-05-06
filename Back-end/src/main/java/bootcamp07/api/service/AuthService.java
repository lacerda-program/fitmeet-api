package bootcamp07.api.service;

import bootcamp07.api.dto.request.LoginRequestDTO;
import bootcamp07.api.dto.request.RegisterRequestDTO;
import bootcamp07.api.dto.response.AchievementResponseDTO;
import bootcamp07.api.dto.response.AuthResponseDTO;
import bootcamp07.api.exception.ConflictException;
import bootcamp07.api.exception.ForbiddenException;
import bootcamp07.api.exception.NotFoundException;
import bootcamp07.api.exception.UnauthorizedException;
import bootcamp07.api.model.User;
import bootcamp07.api.repository.UserRepository;
import bootcamp07.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("O e-mail ou CPF informado já pertence a outro usuário.");
        }
        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("O e-mail ou CPF informado já pertence a outro usuário.");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .password(passwordEncoder.encode(dto.getPassword()))
                .avatar("https://ui-avatars.com/api/?name=" + dto.getName().replace(" ", "+"))
                .build();

        userRepository.save(user);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Senha incorreta.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());

        List<AchievementResponseDTO> achievements = user.getAchievements() == null ? List.of() :
                user.getAchievements().stream()
                .map(ua -> AchievementResponseDTO.builder()
                           .name(ua.getAchievement().getName())
                           .criterion(ua.getAchievement().getCriterion())
                           .build())
                .toList();

        return AuthResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .avatar(user.getAvatar())
                .xp(user.getXp())
                .level(user.getLevel())
                .achievements(achievements)
                .build();
    }
}