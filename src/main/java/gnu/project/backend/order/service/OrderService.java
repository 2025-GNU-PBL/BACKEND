package gnu.project.backend.order.service;

import static gnu.project.backend.common.error.ErrorCode.*;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.order.dto.request.CouponPreviewRequest;
import gnu.project.backend.order.dto.request.OrderCreateRequest;
import gnu.project.backend.order.dto.response.CouponPreviewResponse;
import gnu.project.backend.order.dto.response.OrderResponse;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.entity.OrderDetail;
import gnu.project.backend.order.repository.OrderRepository;
import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.repository.ReservationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final OrderCouponPolicy couponPolicy;

    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders(Accessor accessor) {
        return orderRepository.findAllByCustomerSocialId(accessor.getSocialId())
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findByOrderCode(String orderCode) {
        Order order = orderRepository.findByOrderCodeWithDetails(orderCode)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
        return OrderResponse.from(order);
    }

    public OrderResponse createFromReservation(Accessor accessor, OrderCreateRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND_EXCEPTION));

        if (!reservation.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION);
        }

        if (orderRepository.existsByReservationId(reservation.getId())) {
            throw new BusinessException(DUPLICATE_ORDER_RESERVATION);
        }

        // 수량 1 기준(필요 시 확장)
        OrderDetail detail = OrderDetail.of(reservation.getProduct(), 1);
        long original = detail.getLineTotal();

        Long couponId = request.userCouponId();
        long discount = 0L;
        if (couponId != null) {
            discount = couponPolicy.previewDiscount(reservation, original, couponId, accessor.getSocialId());
        }
        long total = Math.max(0L, original - discount);

        Order order = Order.fromReservation(
                reservation,
                UUID.randomUUID().toString(),
                original,
                discount,
                total,
                List.of(detail)
        );

        if (couponId != null && discount > 0L) {
            order.applyCoupon(couponId, discount);
        } else {
            order.clearCoupon();
        }

        Order saved = orderRepository.save(order);
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CouponPreviewResponse previewCoupon(Accessor accessor, String orderCode, CouponPreviewRequest req) {
        Order order = orderRepository.findByOrderCodeWithDetails(orderCode)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND)); //존재하지 않는 쿠폰
        if (!order.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(CUSTOMER_NOT_VALID_EXCEPTION);
        }
        long discount = couponPolicy.previewDiscount(
                order.getReservation(), order.getOriginalPrice(), req.userCouponId(), accessor.getSocialId()
        );
        long total = Math.max(0L, order.getOriginalPrice() - discount);
        return new CouponPreviewResponse(order.getOriginalPrice(), discount, total);
    }

    public OrderResponse applyCoupon(Accessor accessor, String orderCode, Long userCouponId) {
        Order order = orderRepository.findByOrderCodeWithDetails(orderCode)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
        if (!order.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(CUSTOMER_NOT_VALID_EXCEPTION);
        }
        long discount = couponPolicy.previewDiscount(
                order.getReservation(), order.getOriginalPrice(), userCouponId, accessor.getSocialId()
        );
        order.applyCoupon(userCouponId, discount);
        return OrderResponse.from(order);
    }

    // Toss confirm 성공 webhook에서 호출
    public void onPaymentConfirmed(String orderCode) {
        Order order = orderRepository.findByOrderCodeWithDetails(orderCode)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
        order.markPaid();

        Long couponId = order.getAppliedCustomerCouponId();
        if (couponId != null && order.getDiscountAmount() > 0L) {
            couponPolicy.markUsed(couponId, order.getCustomer().getOauthInfo().getSocialId());
        }
    }
}
