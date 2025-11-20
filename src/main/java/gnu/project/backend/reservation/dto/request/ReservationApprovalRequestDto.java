package gnu.project.backend.reservation.dto.request;

import gnu.project.backend.reservation.enumerated.Status;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationApprovalRequestDto(
    Status status,
    LocalDate reservationStartDate,
    LocalDate reservationEndDate,
    LocalTime reservationStartTime,
    LocalTime reservationEndTime
) {

}
