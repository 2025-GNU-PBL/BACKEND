package gnu.project.backend.notification.event.dto;

import gnu.project.backend.common.enumerated.UserRole;

public record PaymentApprovedEvent(
    Long customerId,
    Long reservationId,
    String title,
    UserRole userRole
) {

}
