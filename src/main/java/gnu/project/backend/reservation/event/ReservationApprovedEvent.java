package gnu.project.backend.reservation.event;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationApprovedEvent(
    Long customerId,
    Long reservationId,
    LocalDate reservationStartDate,
    LocalDate reservationEndDate,
    LocalTime reservationStartTime,
    LocalTime reservationEndTime,

    String title,
    String content
) {

}