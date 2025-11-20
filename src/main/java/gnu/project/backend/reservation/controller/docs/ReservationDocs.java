package gnu.project.backend.reservation.controller.docs;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.reservation.dto.request.ReservationApprovalRequestDto;
import gnu.project.backend.reservation.dto.request.ReservationRejectionRequestDto;
import gnu.project.backend.reservation.dto.request.ReservationRequestDto;
import gnu.project.backend.reservation.dto.response.ReservationDetailResponseDto;
import gnu.project.backend.reservation.dto.response.ReservationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Reservation API", description = "예약 등록·조회·승인·거절 등 예약 관련 API")
public interface ReservationDocs {

    @Operation(
        summary = "예약 등록",
        description = "고객이 새로운 예약을 등록합니다. (고객만 가능)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "예약 등록 성공",
            content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이미 예약이 존재하거나 중복된 시간대")
    })
    ResponseEntity<ReservationResponseDto> enrollReservation(
        @Schema(hidden = true) Accessor accessor,
        @RequestBody ReservationRequestDto request
    );


    @Operation(
        summary = "예약 승인",
        description = "사장님이 예약을 승인합니다. (사장님만 가능)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "예약 승인 성공",
            content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음 (사장님이 아님)"),
        @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 처리된 예약")
    })
    ResponseEntity<ReservationResponseDto> approve(
        @Schema(hidden = true) Accessor accessor,
        @PathVariable Long id,
        @RequestBody ReservationApprovalRequestDto request
    );


    @Operation(
        summary = "예약 거절",
        description = "사장님이 예약을 거절합니다. (사장님만 가능)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "예약 거절 성공",
            content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음 (사장님이 아님)"),
        @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 처리된 예약")
    })
    ResponseEntity<ReservationResponseDto> reject(
        @Schema(hidden = true) Accessor accessor,
        @PathVariable Long id,
        @RequestBody ReservationRejectionRequestDto request
    );


    @Operation(
        summary = "내 예약 목록 조회",
        description = "로그인한 사용자(고객 또는 사장님)의 예약 목록을 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ReservationResponseDto.class)))
    })
    ResponseEntity<List<ReservationResponseDto>> findReservations(
        @Schema(hidden = true) Accessor accessor
    );


    @Operation(
        summary = "예약 상세 조회",
        description = "특정 예약의 상세 정보를 조회합니다. 해당 예약의 고객 또는 사장님만 접근 가능합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ReservationDetailResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음")
    })
    ResponseEntity<ReservationDetailResponseDto> findReservationDetail(
        @Schema(hidden = true) Accessor accessor,
        @PathVariable(name = "reservationId")
        @Schema(description = "예약 ID", example = "123") Long reservationId
    );
}