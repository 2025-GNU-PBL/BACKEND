package gnu.project.backend.reservaiton.dto.request;

import gnu.project.backend.reservaiton.enumerated.Status;

public record ReservationStatusChangeRequestDto(
    Long id,
    Status status
) {

}
