package gnu.project.backend.notification.event.dto;

public record PaymentCancelRequestedEvent(
    Long ownerId,
    Long reservationId,
    String title

) {

}
