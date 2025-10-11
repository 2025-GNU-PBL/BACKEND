package gnu.project.backend.payment.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.common.enumerated.PaymentStatus;
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // 결제 상태 (예: "DONE", "CANCELED")

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
        payment.status = PaymentStatus.fromString(String.valueOf(tossResponse.getStatus()));

        if (tossResponse.getApprovedAt() != null) {
            ZonedDateTime zdt = ZonedDateTime.parse(tossResponse.getApprovedAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            payment.approvedAt = zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }
        payment.receiptUrl = tossResponse.getReceipt() != null ? tossResponse.getReceipt().getUrl() : null;
        return payment;
    }


    public void requestCancel(String reason) {
        if (status != PaymentStatus.DONE)
            throw new IllegalStateException("결제 완료 상태에서만 취소 요청이 가능합니다.");

        this.status = PaymentStatus.CANCEL_REQUESTED;
        this.cancelReason = reason;
        order.updateStatus(OrderStatus.REFUND_REQUESTED);
    }


    public void approveCancel(String reason, LocalDateTime canceledAt) {
        if (status != PaymentStatus.CANCEL_REQUESTED)
            throw new IllegalStateException("취소 요청 상태에서만 승인 가능합니다.");

        this.status = PaymentStatus.CANCELED;
        this.cancelReason = reason;
        this.canceledAt = canceledAt;
        order.updateStatus(OrderStatus.CANCELED);
    }


    public void refund(String reason, LocalDateTime refundedAt) {
        if (status != PaymentStatus.CANCELED && status != PaymentStatus.CANCEL_REQUESTED)
            throw new IllegalStateException("환불은 취소 요청 또는 취소 완료 상태에서만 가능합니다.");

        this.status = PaymentStatus.REFUND_COMPLETED;
        this.cancelReason = reason;
        this.canceledAt = refundedAt;
        order.updateStatus(OrderStatus.REFUNDED);
    }



}



