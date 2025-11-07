package gnu.project.backend.notification.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.notification.dto.response.NotificationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(
    name = "Notification API",
    description = "알림에 관련된 API 문서"
)
public interface NotificationDocs {

    @Operation(
        summary = "SSE 알림 구독",
        description = "클라이언트가 실시간 알림을 수신하기 위해 서버에 연결합니다. Content-Type은 text/event-stream입니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "SSE 연결 성공",
                content = @Content(mediaType = "text/event-stream")
            )
        }
    )
    @GetMapping(value = "/subscribe")
    SseEmitter subscribe(
        @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(
        summary = "읽지 않은 알림 목록 조회",
        description = "현재 로그인한 사용자의 **읽지 않은(isRead=false)** 알림 목록을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "읽지 않은 알림 목록 조회 성공",
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponseDto.class))
                )
            )
        }
    )
    @GetMapping("/unread")
    ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications(
        @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(
        summary = "전체 알림 목록 조회",
        description = "현재 로그인한 사용자의 전체 알림 목록을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "전체 알림 목록 조회 성공",
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponseDto.class))
                )
            )
        }
    )
    @GetMapping()
    ResponseEntity<List<NotificationResponseDto>> getAllNotifications(
        @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(
        summary = "읽지 않은 알림 개수 조회",
        description = "현재 로그인한 사용자의 읽지 않은 알림의 총 개수(Long)를 반환합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "개수 조회 성공",
                content = @Content(schema = @Schema(implementation = Long.class))
            )
        }
    )
    @GetMapping("/unread/count")
    ResponseEntity<Long> getUnreadCount(@Parameter(hidden = true) @Auth Accessor accessor);

    @Operation(
        summary = "특정 알림 읽음 처리",
        description = "특정 ID의 알림을 **읽음 상태(isRead=true)**로 변경합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "읽음 처리 성공",
                content = @Content(schema = @Schema(implementation = NotificationResponseDto.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "알림 ID를 찾을 수 없음"
            )
        }
    )
    @PatchMapping("/{notificationId}/read")
    ResponseEntity<NotificationResponseDto> markAsRead(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Parameter(description = "읽음 처리할 알림 ID", required = true)
        @PathVariable final Long notificationId
    );

    @Operation(
        summary = "모든 알림 읽음 처리",
        description = "현재 로그인한 사용자의 **모든** 읽지 않은 알림을 **읽음 상태(isRead=true)**로 변경합니다. (변경된 알림 목록 반환)",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "모든 알림 읽음 처리 성공",
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponseDto.class))
                )
            )
        }
    )
    @PatchMapping()
    ResponseEntity<List<NotificationResponseDto>> markAllAsRead(
        @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(
        summary = "특정 알림 삭제",
        description = "특정 ID의 알림을 삭제합니다.",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "삭제 성공 (응답 바디 없음)"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "알림 ID를 찾을 수 없음"
            )
        }
    )
    @DeleteMapping("/{notificationId}")
    ResponseEntity<Void> deleteNotification(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Parameter(description = "삭제할 알림 ID", required = true)
        @PathVariable final Long notificationId
    );
}