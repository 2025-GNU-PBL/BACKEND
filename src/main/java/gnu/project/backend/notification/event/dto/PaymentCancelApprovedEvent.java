package gnu.project.backend.notification.event.dto;

public record PaymentCancelApprovedEvent(
    Long customerId,
    Long reservationId,
    String title
) {

}
