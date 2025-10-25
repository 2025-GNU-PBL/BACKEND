package gnu.project.backend.order.entity;

import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.customer.entity.Customer;
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
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name ="order_code", unique = true)
    private String orderCode;

    @Column(name ="total_price")
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    public static Order create(Customer customer, String orderCode, Long totalPrice, List<OrderDetail> orderDetails) {
        Order order = new Order();
        order.customer = customer;
        order.orderCode = orderCode;
        order.totalPrice = totalPrice;
        order.status = OrderStatus.WAITING_FOR_PAYMENT;
        for (OrderDetail detail : orderDetails) {
            order.addOrderDetail(detail);
        }
        return order;
    }
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}
