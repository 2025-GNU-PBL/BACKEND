package gnu.project.backend.notification.event.dto;

public record PaymentApprovedEvent(
    Long customerId,
    Long reservationId,
    String title
) {

}
