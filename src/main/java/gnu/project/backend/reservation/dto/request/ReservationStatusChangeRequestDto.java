package gnu.project.backend.reservation.dto.request;

import gnu.project.backend.reservation.enumerated.Status;

public record ReservationStatusChangeRequestDto(
    Long id,
    Status status
) {

}
