package bootcamp07.api.controller;

import bootcamp07.api.dto.request.ApproveParticipantRequestDTO;
import bootcamp07.api.dto.request.CheckInRequestDTO;
import bootcamp07.api.dto.request.CreateActivityRequestDTO;
import bootcamp07.api.dto.request.UpdateActivityRequestDTO;
import bootcamp07.api.dto.response.*;
import bootcamp07.api.model.User;
import bootcamp07.api.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
@Tag(name = "Atividades", description = "Endpoints de gerenciamento de atividades esportivas")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/types")
    @Operation(summary = "Listar tipos de atividades")
    public ResponseEntity<List<ActivityTypeResponseDTO>> getActivityTypes(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(activityService.getActivityTypes());
    }

    @GetMapping
    @Operation(summary = "Listar atividades com paginação, filtro por tipo e ordenação")
    public ResponseEntity<ActivityPageResponseDTO> getActivities(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) UUID typeId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String order) {
        return ResponseEntity.ok(activityService.getActivities(user, page, pageSize, typeId, orderBy, order));
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todas as atividades com filtro por tipo e ordenação")
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivities(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) UUID typeId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String order) {
        return ResponseEntity.ok(activityService.getAllActivities(user, typeId, orderBy, order));
    }

    @GetMapping("/user/creator")
    @Operation(summary = "Buscar atividades criadas pelo usuário")
    public ResponseEntity<ActivityPageResponseDTO> getActivitiesByCreator(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(activityService.getActivitiesByCreator(user, page, pageSize));
    }

    @GetMapping("/user/creator/all")
    @Operation(summary = "Buscar todas as atividades criadas pelo usuário")
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivitiesByCreator(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(activityService.getAllActivitiesByCreator(user));
    }

    @GetMapping("/user/participant")
    @Operation(summary = "Buscar atividades em que o usuário se inscreveu")
    public ResponseEntity<ActivityPageResponseDTO> getActivitiesByParticipant(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(activityService.getActivitiesByParticipant(user, page, pageSize));
    }

    @GetMapping("/user/participant/all")
    @Operation(summary = "Buscar todas as atividades em que o usuário se inscreveu")
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivitiesByParticipant(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(activityService.getAllActivitiesByParticipant(user));
    }

    @GetMapping("/{id}/participants")
    @Operation(summary = "Buscar participantes de uma atividade")
    public ResponseEntity<List<ParticipantResponseDTO>> getParticipants(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(activityService.getParticipants(user, id));
    }

    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Criar uma atividade")
    public ResponseEntity<ActivityResponseDTO> createActivity(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid CreateActivityRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(activityService.createActivity(user, dto, image));
    }

    @PostMapping("/{id}/subscribe")
    @Operation(summary = "Inscrever-se em uma atividade")
    public ResponseEntity<ActivityResponseDTO> subscribeToActivity(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(activityService.subscribeToActivity(user, id));
    }

    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Editar uma atividade existente")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @ModelAttribute UpdateActivityRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(activityService.updateActivity(user, id, dto, image));
    }

    @PutMapping("/{id}/conclude")
    @Operation(summary = "Concluir uma atividade")
    public ResponseEntity<Map<String, String>> concludeActivity(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        activityService.concludeActivity(user, id);
        return ResponseEntity.ok(Map.of("message", "Atividade concluída com sucesso."));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Aprovar ou negar inscrição de participante em atividade privada")
    public ResponseEntity<Map<String, String>> approveParticipant(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody ApproveParticipantRequestDTO dto) {
        activityService.approveParticipant(user, id, dto);
        return ResponseEntity.ok(Map.of("message", "Solicitação de participação aprovada com sucesso."));
    }

    @PutMapping("/{id}/check-in")
    @Operation(summary = "Fazer check-in em uma atividade usando código de confirmação")
    public ResponseEntity<Map<String, String>> checkIn(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody CheckInRequestDTO dto) {
        activityService.checkIn(user, id, dto.getConfirmationCode());
        return ResponseEntity.ok(Map.of("message", "Participação confirmada com sucesso."));
    }

    @DeleteMapping("/{id}/unsubscribe")
    @Operation(summary = "Cancelar a inscrição do usuário em uma atividade")
    public ResponseEntity<Map<String, String>> unsubscribeFromActivity(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        activityService.unsubscribeFromActivity(user, id);
        return ResponseEntity.ok(Map.of("message", "Participação cancelada com sucesso."));
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Excluir uma atividade existente")
    public ResponseEntity<Map<String, String>> deleteActivity(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        activityService.deleteActivity(user, id);
        return ResponseEntity.ok(Map.of("message", "Atividade excluída com sucesso."));
    }
}
