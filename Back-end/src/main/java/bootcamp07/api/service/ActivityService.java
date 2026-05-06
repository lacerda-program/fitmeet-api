package bootcamp07.api.service;

import bootcamp07.api.dto.request.ApproveParticipantRequestDTO;
import bootcamp07.api.dto.request.CreateActivityRequestDTO;
import bootcamp07.api.dto.request.UpdateActivityRequestDTO;
import bootcamp07.api.dto.response.*;
import bootcamp07.api.exception.*;
import bootcamp07.api.model.*;
import bootcamp07.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final ActivityParticipantRepository activityParticipantRepository;
    private final PreferenceRepository preferenceRepository;
    private final StorageService storageService;
    private final AchievementService achievementService;
    private final UserService userService;

    private static final int XP_PER_CHECKIN = 50;

    public ActivityPageResponseDTO getActivities(User user, int page, int pageSize,
                                                 UUID typeId, String orderBy, String order) {
        Sort sort = order.equalsIgnoreCase("asc") ?
                Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<Activity> activitiesPage;

        if (typeId != null) {
            activitiesPage = activityRepository.findAvailableActivities(typeId, pageable);
        } else {
            List<Preference> preferences = preferenceRepository.findByUserId(user.getId());
            if (!preferences.isEmpty()) {
                UUID preferredTypeId = preferences.get(0).getType().getId();
                activitiesPage = activityRepository.findAvailableActivities(preferredTypeId, pageable);
            } else {
                activitiesPage = activityRepository.findAvailableActivities(null, pageable);
            }
        }

        List<ActivityResponseDTO> activities = activitiesPage.getContent().stream()
                .map(a -> mapToActivityResponseDTO(a, user))
                .toList();

        int totalPages = activitiesPage.getTotalPages();
        return ActivityPageResponseDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .totalActivities(activitiesPage.getTotalElements())
                .totalPages(totalPages)
                .previous(page > 1 ? page - 1 : null)
                .next(page < totalPages ? page + 1 : null)
                .activities(activities)
                .build();
    }

    public List<ActivityResponseDTO> getAllActivities(User user, UUID typeId,
                                                      String orderBy, String order) {
        Sort sort = order.equalsIgnoreCase("asc") ?
                Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();

        return activityRepository.findAllAvailableActivities(typeId).stream()
                .sorted((a, b) -> order.equalsIgnoreCase("asc") ? 1 : -1)
                .map(a -> mapToActivityResponseDTO(a, user))
                .toList();
    }

    public ActivityPageResponseDTO getActivitiesByCreator(User user, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Activity> activitiesPage = activityRepository.findByCreatorId(user.getId(), pageable);

        List<ActivityResponseDTO> activities = activitiesPage.getContent().stream()
                .map(a -> mapToActivityResponseDTO(a, user))
                .toList();

        int totalPages = activitiesPage.getTotalPages();
        return ActivityPageResponseDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .totalActivities(activitiesPage.getTotalElements())
                .totalPages(totalPages)
                .previous(page > 1 ? page - 1 : null)
                .next(page < totalPages ? page + 1 : null)
                .activities(activities)
                .build();
    }

    public List<ActivityResponseDTO> getAllActivitiesByCreator(User user) {
        return activityRepository.findAllByCreatorId(user.getId()).stream()
                .map(a -> mapToActivityResponseDTO(a, user))
                .toList();
    }

    public ActivityPageResponseDTO getActivitiesByParticipant(User user, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ActivityParticipant> participantsPage =
                activityParticipantRepository.findByUserId(user.getId(), pageable);

        List<ActivityResponseDTO> activities = participantsPage.getContent().stream()
                .map(ap -> mapToActivityResponseDTO(ap.getActivity(), user))
                .toList();

        int totalPages = participantsPage.getTotalPages();
        return ActivityPageResponseDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .totalActivities(participantsPage.getTotalElements())
                .totalPages(totalPages)
                .previous(page > 1 ? page - 1 : null)
                .next(page < totalPages ? page + 1 : null)
                .activities(activities)
                .build();
    }

    public List<ActivityResponseDTO> getAllActivitiesByParticipant(User user) {
        return activityParticipantRepository.findAllByUserId(user.getId()).stream()
                .map(ap -> mapToActivityResponseDTO(ap.getActivity(), user))
                .toList();
    }

    public List<ParticipantResponseDTO> getParticipants(User user, UUID activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        return activityParticipantRepository.findByActivityId(activityId).stream()
                .map(ap -> ParticipantResponseDTO.builder()
                        .id(ap.getId())
                        .userId(ap.getUser().getId())
                        .name(ap.getUser().getName())
                        .avatar(ap.getUser().getAvatar())
                        .subscriptionStatus(ap.getConfirmedAt() != null ? "CONFIRMED" :
                                ap.getApproved() ? "APPROVED" : "PENDING")
                        .confirmedAt(ap.getConfirmedAt())
                        .build())
                .toList();
    }

    @Transactional
    public ActivityResponseDTO createActivity(User user, CreateActivityRequestDTO dto,
                                              MultipartFile image) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        ActivityType type = activityTypeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new NotFoundException("Tipo de atividade não encontrado."));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = storageService.uploadImage(image);
        }

        String confirmationCode = generateConfirmationCode();

        Activity activity = Activity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .type(type)
                .confirmationCode(confirmationCode)
                .image(imageUrl)
                .scheduledDate(dto.getScheduledDate())
                .private_(dto.getIsPrivate())
                .creator(user)
                .build();

        Activity saved = activityRepository.save(activity);

        achievementService.grantFirstActivity(user);

        return mapToActivityResponseDTO(saved, user);
    }

    @Transactional
    public ActivityResponseDTO updateActivity(User user, UUID activityId,
                                              UpdateActivityRequestDTO dto, MultipartFile image) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        if (!activity.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("Apenas o criador da atividade pode editá-la.");
        }

        if (dto.getTitle() != null) activity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) activity.setDescription(dto.getDescription());
        if (dto.getScheduledDate() != null) activity.setScheduledDate(dto.getScheduledDate());
        if (dto.getIsPrivate() != null) activity.setPrivate_(dto.getIsPrivate());

        if (dto.getTypeId() != null) {
            ActivityType type = activityTypeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new NotFoundException("Tipo de atividade não encontrado."));
            activity.setType(type);
        }

        if (image != null && !image.isEmpty()) {
            if (activity.getImage() != null) {
                storageService.deleteImage(activity.getImage());
            }
            activity.setImage(storageService.uploadImage(image));
        }

        return mapToActivityResponseDTO(activityRepository.save(activity), user);
    }

    public ActivityResponseDTO subscribeToActivity(User user, UUID activityId) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        if (activity.getCompletedAt() != null) {
            throw new BusinessException("Não é possível se inscrever em uma atividade concluída.",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if (activity.getCreator().getId().equals(user.getId())) {
            throw new BusinessException("O criador da atividade não pode se inscrever como um participante.",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if (activityParticipantRepository.existsByActivityIdAndUserId(activityId, user.getId())) {
            throw new ConflictException("Você já se registrou nesta atividade.");
        }

        ActivityParticipant participant = ActivityParticipant.builder()
                .activity(activity)
                .user(user)
                .approved(!activity.getPrivate_())
                .build();

        ActivityParticipant saved = activityParticipantRepository.save(participant);

        return mapToSubscribeResponseDTO(saved);
    }

    @Transactional
    public void checkIn(User user, UUID activityId, String confirmationCode) {
        if (user.getDeletedAt() != null) {
            throw new ForbiddenException("Esta conta foi desativada e não pode ser utilizada.");
        }

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        if (activity.getCompletedAt() != null) {
            throw new BusinessException("Não é possível confirmar presença em uma atividade concluída.",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        ActivityParticipant participant = activityParticipantRepository
                .findByActivityIdAndUserId(activityId, user.getId())
                .orElseThrow(() -> new BusinessException(
                        "Apenas participantes aprovados na atividade podem fazer check-in.",
                        org.springframework.http.HttpStatus.BAD_REQUEST));

        if (!participant.getApproved()) {
            throw new BusinessException("Apenas participantes aprovados na atividade podem fazer check-in.",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if (participant.getConfirmedAt() != null) {
            throw new ConflictException("Você já confirmou sua participação nesta atividade.");
        }

        if (!activity.getConfirmationCode().equals(confirmationCode)) {
            throw new BusinessException("Código de confirmação incorreto.",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        participant.setConfirmedAt(LocalDateTime.now());
        activityParticipantRepository.save(participant);

        userService.addXp(user, XP_PER_CHECKIN);
        userService.addXp(activity.getCreator(), XP_PER_CHECKIN);

        achievementService.grantFirstCheckIn(user);
    }

    public void approveParticipant(User user, UUID activityId,
                                   ApproveParticipantRequestDTO dto) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        if (!activity.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("Apenas o criador da atividade pode aprovar ou negar participantes.");
        }

        ActivityParticipant participant = activityParticipantRepository
                .findById(dto.getParticipantId())
                .orElseThrow(() -> new NotFoundException("Participante não encontrado."));

        participant.setApproved(dto.getApproved());
        activityParticipantRepository.save(participant);
    }

    public void concludeActivity(User user, UUID activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        if (!activity.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("Apenas o criador da atividade pode concluí-la.");
        }

        activity.setCompletedAt(LocalDateTime.now());
        activityRepository.save(activity);

        achievementService.grantFirstComplete(user);
    }

    public void deleteActivity(User user, UUID activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        if (!activity.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("Apenas o criador da atividade pode excluí-la.");
        }

        activity.setDeletedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }

    public void unsubscribeFromActivity(User user, UUID activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        ActivityParticipant participant = activityParticipantRepository
                .findByActivityIdAndUserId(activityId, user.getId())
                .orElseThrow(() -> new BusinessException("Você não se inscreveu nesta atividade.",
                        org.springframework.http.HttpStatus.BAD_REQUEST));

        if (participant.getConfirmedAt() != null) {
            throw new BusinessException(
                    "Não é possível cancelar sua inscrição, pois sua presença já foi confirmada.",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        activityParticipantRepository.delete(participant);
    }

    public List<ActivityTypeResponseDTO> getActivityTypes() {
        return activityTypeRepository.findAll().stream()
                .map(t -> ActivityTypeResponseDTO.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .description(t.getDescription())
                        .image(t.getImage())
                        .build())
                .toList();
    }

    private String generateConfirmationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    private ActivityResponseDTO mapToActivityResponseDTO(Activity activity, User user) {
        String subscriptionStatus = null;
        if (user != null) {
            Optional<ActivityParticipant> participant = activityParticipantRepository
                    .findByActivityIdAndUserId(activity.getId(), user.getId());
            if (participant.isPresent()) {
                ActivityParticipant ap = participant.get();
                subscriptionStatus = ap.getConfirmedAt() != null ? "CONFIRMED" :
                        ap.getApproved() ? "APPROVED" : "PENDING";
            }
        }

        String confirmationCode = null;
        if (user != null && activity.getCreator().getId().equals(user.getId())) {
            confirmationCode = activity.getConfirmationCode();
        }

        AddressResponseDTO address = null;
        if (activity.getAddress() != null) {
            address = AddressResponseDTO.builder()
                    .latitude(activity.getAddress().getLatitude())
                    .longitude(activity.getAddress().getLongitude())
                    .build();
        }

        return ActivityResponseDTO.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .type(activity.getType().getName())
                .image(activity.getImage())
                .confirmationCode(confirmationCode)
                .participantCount(activityParticipantRepository.findByActivityId(activity.getId()).size())
                .address(address)
                .scheduledDate(activity.getScheduledDate())
                .createdAt(activity.getCreatedAt())
                .completedAt(activity.getCompletedAt())
                .private_(activity.getPrivate_())
                .creator(CreatorResponseDTO.builder()
                        .id(activity.getCreator().getId())
                        .name(activity.getCreator().getName())
                        .avatar(activity.getCreator().getAvatar())
                        .build())
                .userSubscriptionStatus(subscriptionStatus)
                .build();
    }

    private ActivityResponseDTO mapToSubscribeResponseDTO(ActivityParticipant participant) {
        return ActivityResponseDTO.builder()
                .id(participant.getId())
                .userSubscriptionStatus(participant.getApproved() ? "APPROVED" : "PENDING")
                .build();
    }
}