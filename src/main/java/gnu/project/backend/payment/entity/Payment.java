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

    private String cancelRejectReason;
    private LocalDateTime cancelRejectAt;

    @Setter
    private LocalDateTime canceledAt;

    public static Payment create(
            Order order,
            String paymentKey,
            String pgProvider,
            String paymentMethod,
            Long amount,
            PaymentStatus status,
            LocalDateTime approvedAt,
            String receiptUrl
    ) {
        Payment p = new Payment();
        p.order = order;
        p.paymentKey = paymentKey;
        p.pgProvider = pgProvider;
        p.paymentMethod = paymentMethod;
        p.amount = amount;
        p.status = status;
        p.approvedAt = approvedAt;
        p.receiptUrl = receiptUrl;
        return p;
    }

    public void requestCancel(String reason) {
        if (this.status != PaymentStatus.DONE) {
            throw new IllegalStateException("결제 완료 상태에서만 취소 요청 가능");
        }
        this.status = PaymentStatus.CANCEL_REQUESTED;
        this.cancelReason = reason;
        this.order.updateStatus(OrderStatus.CANCEL_REQUESTED);
        this.requestedAt = LocalDateTime.now();
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

    public void rejectCancel(String rejectReason, LocalDateTime rejectedAt){
        if(this.status != PaymentStatus.CANCEL_REQUESTED){
            throw new IllegalStateException("취소 요청 상태에서만 거절 가능");
        }
        this.status = PaymentStatus.DONE;
        this.cancelRejectReason = rejectReason;
        this.cancelRejectAt = rejectedAt;
        this.cancelReason = null;
        this.order.updateStatus(OrderStatus.PAID);
    }


}
