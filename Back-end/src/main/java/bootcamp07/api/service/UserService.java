package bootcamp07.api.service;

import bootcamp07.api.dto.request.UpdateUserRequestDTO;
import bootcamp07.api.dto.response.AchievementResponseDTO;
import bootcamp07.api.dto.response.PreferenceResponseDTO;
import bootcamp07.api.dto.response.UserResponseDTO;
import bootcamp07.api.exception.ConflictException;
import bootcamp07.api.exception.ForbiddenException;
import bootcamp07.api.exception.NotFoundException;
import bootcamp07.api.model.Preference;
import bootcamp07.api.model.User;
import bootcamp07.api.repository.ActivityTypeRepository;
import bootcamp07.api.repository.PreferenceRepository;
import bootcamp07.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PreferenceRepository preferenceRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;
    private final AchievementService achievementService;

    public UserResponseDTO getUser(User user) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        List<AchievementResponseDTO> achievements = user.getAchievements() == null ? List.of() :
                user.getAchievements().stream()
                .map(ua -> AchievementResponseDTO.builder()
                           .name(ua.getAchievement().getName())
                           .criterion(ua.getAchievement().getCriterion())
                           .build())
                .toList();

        return UserResponseDTO.builder()
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

    public UserResponseDTO updateUser(User user, UpdateUserRequestDTO dto) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ConflictException("O e-mail ou CPF informado já pertence a outro usuário.");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return mapToUserResponseDTO(userRepository.save(user));
    }

    public String updateAvatar(User user, MultipartFile file) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        if (user.getAvatar() != null && !user.getAvatar().contains("ui-avatars.com")) {
            storageService.deleteImage(user.getAvatar());
        }

        String avatarUrl = storageService.uploadImage(file);
        user.setAvatar(avatarUrl);
        userRepository.save(user);

        achievementService.grantFirstAvatar(user);

        return avatarUrl;
    }

    @Transactional
    public void definePreferences(User user, List<UUID> typeIds) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        typeIds.forEach(typeId -> {
            if (!activityTypeRepository.existsById(typeId)) {
                throw new NotFoundException("Um ou mais IDs informados são inválidos.");
            }
        });

        preferenceRepository.deleteByUserId(user.getId());

        typeIds.forEach(typeId -> {
            Preference preference = Preference.builder()
                    .user(user)
                    .type(activityTypeRepository.findById(typeId).get())
                    .build();
            preferenceRepository.save(preference);
        });
    }

    public List<PreferenceResponseDTO> getPreferences(User user) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        return preferenceRepository.findByUserId(user.getId()).stream()
                .map(p -> PreferenceResponseDTO.builder()
                        .typeId(p.getType().getId())
                        .typeName(p.getType().getName())
                        .typeDescription(p.getType().getDescription())
                        .build())
                .toList();
    }

    public void deactivateAccount(User user) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void addXp(User user, int xpAmount) {
        user.setXp(user.getXp() + xpAmount);

        int xpPerLevel = 100;
        int newLevel = (user.getXp() / xpPerLevel) + 1;

        if (newLevel > user.getLevel()) {
            user.setLevel(newLevel);
            achievementService.grantFirstLevelUp(user);
        }

        userRepository.save(user);
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        List<AchievementResponseDTO> achievements = user.getAchievements() == null ? List.of() :
                user.getAchievements().stream()
                .map(ua -> AchievementResponseDTO.builder()
                           .name(ua.getAchievement().getName())
                           .criterion(ua.getAchievement().getCriterion())
                           .build())
                .toList();

        return UserResponseDTO.builder()
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
