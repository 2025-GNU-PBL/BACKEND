package gnu.project.backend.reservation.controller.docs;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.reservation.dto.request.ReservationRequestDto;
import gnu.project.backend.reservation.dto.request.ReservationStatusChangeRequestDto;
import gnu.project.backend.reservation.dto.response.ReservationDetailResponseDto;
import gnu.project.backend.reservation.dto.response.ReservationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Reservation API", description = "예약 관련 API")
public interface ReservationDocs {

    @Operation(
        summary = "예약 등록",
        description = "고객이 새로운 예약을 등록합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "예약이 성공적으로 등록됨",
                content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))
            )
        }
    )
    @PostMapping()
    ResponseEntity<ReservationResponseDto> enrollReservation(
        Accessor accessor,
        ReservationRequestDto requestDto
    );

    @Operation(
        summary = "예약 상태 변경",
        description = "예약의 상태(예: 대기 → 확정 → 취소)를 변경합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "상태 변경 완료",
                content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))
            )
        }
    )
    @PatchMapping()
    ResponseEntity<ReservationResponseDto> changeReservationStatus(
        Accessor accessor,
        ReservationStatusChangeRequestDto requestDto
    );

    @Operation(
        summary = "예약 목록 조회",
        description = "현재 로그인한 사용자의 예약 목록을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))
            )
        }
    )
    @GetMapping()
    ResponseEntity<List<ReservationResponseDto>> findReservations(
        Accessor accessor
    );

    @Operation(
        summary = "예약 상세 조회",
        description = "특정 예약 ID에 해당하는 예약의 상세 정보를 조회합니다. " +
            "해당 예약의 소유자(고객 또는 사장)만 접근할 수 있습니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ReservationDetailResponseDto.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "예약을 찾을 수 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "403",
                description = "해당 예약에 접근할 권한이 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @GetMapping("/{reservationId}")
    ResponseEntity<ReservationDetailResponseDto> findReservationDetail(
        Accessor accessor,
        @Schema(description = "조회할 예약의 ID", example = "123")
        @PathVariable(name = "reservationId") Long reservationId
    );
}
