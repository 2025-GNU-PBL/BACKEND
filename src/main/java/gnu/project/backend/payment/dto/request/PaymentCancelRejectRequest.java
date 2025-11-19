package gnu.project.backend.payment.dto.request;

public record PaymentCancelRejectRequest(
        String rejectReason
) {
}
