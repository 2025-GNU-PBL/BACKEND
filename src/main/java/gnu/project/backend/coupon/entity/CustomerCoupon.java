package gnu.project.backend.coupon.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.coupon.enumerated.UserCouponStatus;
import gnu.project.backend.customer.entity.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "customer_coupon")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserCouponStatus status = UserCouponStatus.AVAILABLE;
    @CreatedDate
    @Column(name = "downloaded_at", nullable = false, updatable = false)
    private LocalDateTime downloadedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    private CustomerCoupon(
        Customer customer,
        Coupon coupon,
        UserCouponStatus userCouponStatus
    ) {
        this.customer = customer;
        this.coupon = coupon;
        this.status = userCouponStatus;
    }

    public static CustomerCoupon ofCreate(
        final Customer customer,
        final Coupon coupon,
        final UserCouponStatus userCouponStatus
    ) {
        return new CustomerCoupon(
            customer,
            coupon,
            userCouponStatus
        );
    }


    public void markAsUsed() {
        this.status = UserCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = UserCouponStatus.EXPIRED;
    }

    public void cancel() {
        this.status = UserCouponStatus.CANCELLED;
    }
}