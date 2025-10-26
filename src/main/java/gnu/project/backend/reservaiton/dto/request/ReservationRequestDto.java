package gnu.project.backend.reservaiton.dto.request;

import gnu.project.backend.reservaiton.enumerated.Status;
import java.time.LocalDateTime;

public record ReservationRequestDto(
    Long productId,
    Status status,
    LocalDateTime reservationTime,
    String title,
    String content
) {

}
