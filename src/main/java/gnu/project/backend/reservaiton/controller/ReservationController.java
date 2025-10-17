package gnu.project.backend.reservaiton.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.reservaiton.dto.request.ReservationRequestDto;
import gnu.project.backend.reservaiton.dto.request.ReservationStatusChangeRequestDto;
import gnu.project.backend.reservaiton.dto.response.ReservationResponseDto;
import gnu.project.backend.reservaiton.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<?> enrollReservation(
        @Auth final Accessor accessor,
        final ReservationRequestDto requestDto
    ) {
        return ResponseEntity.ok(reservationService.createReservation(accessor, requestDto));
    }

    @PatchMapping()
    public ResponseEntity<ReservationResponseDto> changeReservationStatus(
        @Auth final Accessor accessor,
        final ReservationStatusChangeRequestDto requestDto
    ) {
        return ResponseEntity.ok(reservationService.changeStatus(accessor, requestDto));
    }
}
