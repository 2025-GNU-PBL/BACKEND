package gnu.project.backend.reservaiton.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.reservaiton.controller.docs.ReservationDocs;
import gnu.project.backend.reservaiton.dto.request.ReservationRequestDto;
import gnu.project.backend.reservaiton.dto.request.ReservationStatusChangeRequestDto;
import gnu.project.backend.reservaiton.dto.response.ReservationResponseDto;
import gnu.project.backend.reservaiton.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController implements ReservationDocs {

    private final ReservationService reservationService;

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
}
