package gnu.project.backend.reservation.dto.request;

import gnu.project.backend.reservation.enumerated.Status;
import java.time.LocalDate;

public record ReservationRequestDto(
    Long productId,
    Status status,
    LocalDate reservationTime,
    String title,
    String content
) {

}
