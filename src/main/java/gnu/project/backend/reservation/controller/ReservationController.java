package gnu.project.backend.reservation.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.aop.OnlyCustomer;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.reservation.controller.docs.ReservationDocs;
import gnu.project.backend.reservation.dto.request.ReservationRequestDto;
import gnu.project.backend.reservation.dto.request.ReservationStatusChangeRequestDto;
import gnu.project.backend.reservation.dto.response.ReservationDetailResponseDto;
import gnu.project.backend.reservation.dto.response.ReservationResponseDto;
import gnu.project.backend.reservation.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController implements ReservationDocs {

    private final ReservationService reservationService;

    @OnlyCustomer
    @PostMapping()
    public ResponseEntity<ReservationResponseDto> enrollReservation(
        @Auth final Accessor accessor,
        @RequestBody final ReservationRequestDto request
    ) {
        return ResponseEntity.ok(reservationService.createReservation(accessor, request));
    }

    @PatchMapping()
    public ResponseEntity<ReservationResponseDto> changeReservationStatus(
        @Auth final Accessor accessor,
        @RequestBody final ReservationStatusChangeRequestDto request
    ) {
        return ResponseEntity.ok(reservationService.changeStatus(accessor, request));
    }

    @GetMapping()
    public ResponseEntity<List<ReservationResponseDto>> findReservations(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(reservationService.findReservations(accessor));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponseDto> findReservationDetail(
        @Auth final Accessor accessor,
        @PathVariable(name = "reservationId") final Long reservationId
    ) {
        return ResponseEntity.ok(reservationService.findReservationDetail(accessor, reservationId));
    }
}
