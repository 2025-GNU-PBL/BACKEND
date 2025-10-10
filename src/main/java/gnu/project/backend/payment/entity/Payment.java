package gnu.project.backend.payment.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.payment.dto.response.TossPaymentConfirmResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private String paymentKey; // 토스페이먼츠의 결제 키

    @Column(nullable = false)
    private String pgProvider; // 결제 대행사 (예: "tosspayments")

    @Column(nullable = false)
    private String paymentMethod; // 결제 수단 (예: "카드")

    @Column(nullable = false)
    private Long amount; // 결제 금액

    @Setter
    @Column(nullable = false)
    private String status; // 결제 상태 (예: "DONE", "CANCELED")

    private String transactionKey; // PG사 거래 키

    @Column(columnDefinition = "TEXT") // JSON 형태의 문자열을 저장하기 위해
    private String paymentDetails; // 결제 수단별 상세 정보

    private String failureCode; // 결제 실패 시 에러 코드
    private String failureMessage; // 결제 실패 시 에러 메시지

    private LocalDateTime requestedAt; // 결제 요청 시각
    @Setter
    private LocalDateTime approvedAt; // 결제 승인 시각

    private String receiptUrl; // 영수증 URL

    //결제 취소 관련 필드
    @Setter
    private String cancelReason; // 취소 사유
    @Setter
    private LocalDateTime canceledAt; // 취소 시각


    public static Payment create(Order order, TossPaymentConfirmResponse tossResponse) {
        Payment payment = new Payment();
        payment.order = order;
        payment.paymentKey = tossResponse.getPaymentKey();
        payment.pgProvider = "tosspayments";
        payment.paymentMethod = tossResponse.getMethod();
        payment.amount = tossResponse.getTotalAmount();
        payment.status = tossResponse.getStatus();
        payment.receiptUrl = tossResponse.getReceipt() != null ? tossResponse.getReceipt().getUrl() : null;

        // ZonedDateTime을 LocalDateTime으로 변환
        if (tossResponse.getApprovedAt() != null) {
            ZonedDateTime zdt = ZonedDateTime.parse(tossResponse.getApprovedAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            payment.approvedAt = zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }

        return payment;
    }

    public void updateCancelStatus(String status, String cancelReason, ZonedDateTime canceledAt) {
        this.status = status;
        this.cancelReason = cancelReason;
        if (canceledAt != null) {
            this.approvedAt = canceledAt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }
    }

    public void cancel(String reason) {
        this.status = "CANCELED";
        this.cancelReason = reason;
        this.canceledAt = LocalDateTime.now();
    }


}
