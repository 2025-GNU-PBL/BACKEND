package gnu.project.backend.reservation.dto.response;

import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.enumerated.Status;
import java.time.LocalDate;

public record ReservationResponseDto(
    Long id,
    Long ownerId,
    Long customerId,
    Long productId,
    Status status,
    LocalDate reservationTime,
    String title,
    String content
) {

    public static ReservationResponseDto from(
        final Reservation reservation
    ) {
        return new ReservationResponseDto(
            reservation.getId(),
            reservation.getOwner() != null ? reservation.getOwner().getId() : null,
            reservation.getCustomer() != null ? reservation.getCustomer().getId() : null,
            reservation.getProduct() != null ? reservation.getProduct().getId() : null,
            reservation.getStatus(),
            reservation.getReservationTime(),
            reservation.getTitle(),
            reservation.getContent()
        );
    }
}
