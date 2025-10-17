package gnu.project.backend.reservaiton.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.reservaiton.dto.request.ReservationRequestDto;
import gnu.project.backend.reservaiton.dto.request.ReservationStatusChangeRequestDto;
import gnu.project.backend.reservaiton.dto.response.ReservationResponseDto;
import gnu.project.backend.reservaiton.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
        summary = "예약 등록",
        description = "고객이 새로운 예약을 등록합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "예약이 성공적으로 등록됨",
                content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))
            ),
        }
    )
    @PostMapping()
    public ResponseEntity<ReservationResponseDto> enrollReservation(
        @Auth final Accessor accessor,
        final ReservationRequestDto requestDto
    ) {
        return ResponseEntity.ok(reservationService.createReservation(accessor, requestDto));
    }

    @Operation(
        summary = "예약 상태 변경",
        description = "예약의 상태(예: 대기 → 확정 → 취소)를 변경합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "상태 변경 완료",
                content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))
            ),
        }
    )
    @PatchMapping()
    public ResponseEntity<ReservationResponseDto> changeReservationStatus(
        @Auth final Accessor accessor,
        final ReservationStatusChangeRequestDto requestDto
    ) {
        return ResponseEntity.ok(reservationService.changeStatus(accessor, requestDto));
    }

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
    public ResponseEntity<List<ReservationResponseDto>> findReservations(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(reservationService.findReservations(accessor));
    }
}
