package bootcamp07.api.service;

import bootcamp07.api.model.Achievement;
import bootcamp07.api.model.User;
import bootcamp07.api.model.UserAchievement;
import bootcamp07.api.repository.AchievementRepository;
import bootcamp07.api.repository.UserAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    private static final String FIRST_CHECKIN = "Primeiro Check-in";
    private static final String FIRST_ACTIVITY = "Criador de Atividades";
    private static final String FIRST_COMPLETE = "Atividade Concluída";
    private static final String FIRST_LEVEL_UP = "Subiu de Nível";
    private static final String FIRST_AVATAR = "Nova Foto";

    public void grantFirstCheckIn(User user) {
        grantIfNotExists(user, FIRST_CHECKIN);
    }

    public void grantFirstActivity(User user) {
        grantIfNotExists(user, FIRST_ACTIVITY);
    }

    public void grantFirstComplete(User user) {
        grantIfNotExists(user, FIRST_COMPLETE);
    }

    public void grantFirstLevelUp(User user) {
        grantIfNotExists(user, FIRST_LEVEL_UP);
    }

    public void grantFirstAvatar(User user) {
        grantIfNotExists(user, FIRST_AVATAR);
    }

    private void grantIfNotExists(User user, String achievementName) {
        List<Achievement> achievements = achievementRepository.findAll();

        achievements.stream()
                .filter(a -> a.getName().equals(achievementName))
                .findFirst()
                .ifPresent(achievement -> {
                    boolean alreadyHas = userAchievementRepository
                            .existsByUserIdAndAchievementId(user.getId(), achievement.getId());

                    if (!alreadyHas) {
                        UserAchievement userAchievement = UserAchievement.builder()
                                .user(user)
                                .achievement(achievement)
                                .build();
                        userAchievementRepository.save(userAchievement);
                    }
                });
    }
}