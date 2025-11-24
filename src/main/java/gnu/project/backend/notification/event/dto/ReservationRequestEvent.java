package gnu.project.backend.notification.event.dto;

public record ReservationRequestEvent(
    Long ownerId,
    Long reservationId,
    String title
) {

}
