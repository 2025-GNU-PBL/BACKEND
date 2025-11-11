package gnu.project.backend.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentConfirmRequest(
        String paymentKey,
        @JsonProperty("orderId") String orderCode,
        Long amount
) {}
