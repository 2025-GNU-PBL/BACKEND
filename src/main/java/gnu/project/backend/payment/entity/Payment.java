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

    // 주문 1 : 결제 1
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String pgProvider;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private Long amount;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String transactionKey;

    @Column(columnDefinition = "TEXT")
    private String paymentDetails;

    private String failureCode;
    private String failureMessage;

    private LocalDateTime requestedAt;

    @Setter
    private LocalDateTime approvedAt;

    private String receiptUrl;

    @Setter
    private String cancelReason;

    @Setter
    private LocalDateTime canceledAt;

    public static Payment create(Order order, TossPaymentConfirmResponse toss) {
        Payment p = new Payment();
        p.order = order;
        p.paymentKey = toss.getPaymentKey();
        p.pgProvider = "tosspayments";
        p.paymentMethod = toss.getMethod();
        p.amount = toss.getTotalAmount();
        p.status = PaymentStatus.fromString(String.valueOf(toss.getStatus()));

        if (toss.getApprovedAt() != null) {
            ZonedDateTime zdt = ZonedDateTime.parse(
                    toss.getApprovedAt(),
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
            );
            p.approvedAt = zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }
        p.receiptUrl = toss.getReceipt() != null ? toss.getReceipt().getUrl() : null;
        return p;
    }

    public void requestCancel(String reason) {
        if (this.status != PaymentStatus.DONE) {
            throw new IllegalStateException("결제 완료 상태에서만 취소 요청 가능");
        }
        this.status = PaymentStatus.CANCEL_REQUESTED;
        this.cancelReason = reason;
        this.order.updateStatus(OrderStatus.REFUND_REQUESTED);
    }

    public void approveCancel(String reason, LocalDateTime canceledAt) {
        if (this.status != PaymentStatus.CANCEL_REQUESTED) {
            throw new IllegalStateException("취소 요청 상태에서만 승인 가능");
        }
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = reason;
        this.canceledAt = canceledAt;
        this.order.updateStatus(OrderStatus.CANCELED);
    }

    public void refund(String reason, LocalDateTime refundedAt) {
        if (this.status != PaymentStatus.CANCELED && this.status != PaymentStatus.CANCEL_REQUESTED) {
            throw new IllegalStateException("환불은 취소 상태에서만 가능");
        }
        this.status = PaymentStatus.REFUND_COMPLETED;
        this.cancelReason = reason;
        this.canceledAt = refundedAt;
        this.order.updateStatus(OrderStatus.REFUNDED);
    }
}
