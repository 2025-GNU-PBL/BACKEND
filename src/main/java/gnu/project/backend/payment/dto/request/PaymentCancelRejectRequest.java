package gnu.project.backend.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PaymentCancelRejectRequest(
        @NotBlank(message = "거절 사유는 필수 입니다.")
        String rejectReason
) {
}
