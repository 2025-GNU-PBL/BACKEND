package gnu.project.backend.reservation.dto.request;

import gnu.project.backend.reservation.enumerated.Status;
import java.time.LocalDateTime;

public record ReservationRequestDto(
    Long productId,
    Status status,
    LocalDateTime reservationTime,
    String title,
    String content
) {

}
