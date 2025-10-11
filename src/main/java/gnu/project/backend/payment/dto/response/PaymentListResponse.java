package gnu.project.backend.payment.dto.response;


import gnu.project.backend.common.enumerated.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentListResponse {
    private String orderCode;
    private String productName;
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime approvedAt;
}
