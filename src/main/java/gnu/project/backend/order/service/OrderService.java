package gnu.project.backend.order.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.order.dto.response.OrderResponse;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.entity.OrderDetail;
import gnu.project.backend.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    //Reservation에서 예약 승인시 호출하여 사용
//    public Order createOrderFrom(Reservation reservation) {
//
//        OrderDetail orderDetail = OrderDetail.create(reservation.getProduct());
//
//
//        String orderCode = UUID.randomUUID().toString();
//
//        Order order = Order.create(
//                reservation.getCustomer(),
//                orderCode,
//                orderDetail.getFinalPrice(),
//                List.of(orderDetail)
//        );
//
//
//        return orderRepository.save(order);
//    }

@Transactional(readOnly = true)
public List<OrderResponse> findMyOrders(Accessor accessor) {

    List<Order> orders = orderRepository.findByCustomer_Id(Long.valueOf(accessor.getSocialId()));
    return orders.stream()
            .map(OrderResponse::new)
            .toList();
}
}
