package gnu.project.backend.order.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.reservation.entity.Reservation;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name ="order_code", unique = true, nullable = false)
    private String orderCode;

    @Column(name = "original_price", nullable = false)
    private Long originalPrice;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name ="total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "applied_customer_coupon_id")
    private Long appliedCustomerCouponId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private Order(
            Reservation reservation,
            Customer customer,
            String orderCode,
            Long originalPrice,
            Long discountAmount,
            Long totalPrice
    ) {
        this.reservation = reservation;
        this.customer = customer;
        this.orderCode = orderCode;
        this.originalPrice = originalPrice;
        this.discountAmount = discountAmount != null ? discountAmount : 0L;
        this.totalPrice = totalPrice != null ? totalPrice : originalPrice - this.discountAmount;
        this.status = OrderStatus.WAITING_FOR_PAYMENT;
    }

    public static Order fromReservation(
            Reservation reservation,
            String orderCode,
            Long originalPrice,
            Long discountAmount,
            Long totalPrice,
            List<OrderDetail> details
    ) {
        Order order = new Order(
                reservation,
                reservation.getCustomer(),
                orderCode,
                originalPrice,
                discountAmount,
                totalPrice
        );
        details.forEach(order::addOrderDetail);
        return order;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void applyCoupon(Long customerCouponId, long discountAmount) {
        this.appliedCustomerCouponId = customerCouponId;
        this.discountAmount = Math.max(0L, discountAmount);
        this.totalPrice = Math.max(0L, this.originalPrice - this.discountAmount);
    }

    public void clearCoupon() {
        this.appliedCustomerCouponId = null;
        this.discountAmount = 0L;
        this.totalPrice = this.originalPrice;
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public String getShopName() {
        return (reservation != null && reservation.getProduct() != null
                && reservation.getProduct().getOwner() != null)
                ? reservation.getProduct().getOwner().getBzName()
                : "알 수 없음";
    }

    public String getThumbnailUrl() {
        if (orderDetails != null && !orderDetails.isEmpty()) {
            return orderDetails.get(0).getDisplayThumbnailUrl();
        }
        return null;
    }


}
