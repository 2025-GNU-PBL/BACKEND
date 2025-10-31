package gnu.project.backend.order.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약에서 온 주문일 수도 있고 아닐 수도 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name ="order_code", unique = true, nullable = false)
    private String orderCode;

    // 쿠폰 적용 전
    @Column(name = "original_price", nullable = false)
    private Long originalPrice;

    // 쿠폰으로 깎은 금액
    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    // 실제 결제할 금액
    @Column(name ="total_price", nullable = false)
    private Long totalPrice;

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
        this.discountAmount = discountAmount;
        this.totalPrice = totalPrice;
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
}
