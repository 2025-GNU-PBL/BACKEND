package gnu.project.backend.reservation.event;

import java.time.LocalDate;

public record ReservationApprovedEvent(
    Long reservationId,
    LocalDate reservationTime,
    String title,
    String content
) {

}