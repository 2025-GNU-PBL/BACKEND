package gnu.project.backend.order.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.entity.CustomerCoupon;
import gnu.project.backend.coupon.enumerated.UserCouponStatus;
import gnu.project.backend.coupon.repository.CustomerCouponRepository;
import gnu.project.backend.order.dto.request.OrderCreateRequest;
import gnu.project.backend.order.dto.response.OrderResponse;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.entity.OrderDetail;
import gnu.project.backend.order.repository.OrderRepository;
import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static gnu.project.backend.common.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    //private final CustomerCouponRepository customerCouponRepository;

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

    @Transactional
    public OrderResponse createFromReservation(Accessor accessor, OrderCreateRequest request) {

        // 1. 예약 가져오기
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND_EXCEPTION));

        // 2. 예약의 주인과 지금 토큰의 주인이 같은지
        if (!reservation.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION);
        }

        // 3. 원가 = 예약된 상품의 가격
        Long originalPrice = reservation.getProduct().getPrice().longValue();

        // 4. 쿠폰 적용
//        Long discount = 0L;
//        if (request.userCouponId() != null) {
//            CustomerCoupon customerCoupon = customerCouponRepository.findByIdWithCoupon(request.userCouponId())
//                    .orElseThrow(() -> new BusinessException(CUSTOMER_COUPON_NOT_FOUND));
//
//            // 내 쿠폰인지 검사
//            if (!customerCoupon.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
//                throw new BusinessException(IS_NOT_VALID_CUSTOMER);
//            }
//
//            Coupon coupon = customerCoupon.getCoupon();
//
//            // 여기서 쿠폰 조건 검사 (너희 서비스하고 온전히 똑같이 가려면 공통 메서드로 빼도 됨)
//            if (!coupon.isUsable()) {
//                throw new BusinessException(COUPON_NOT_AVAILABLE);
//            }
//            if (coupon.getProduct() != null
//                    && !coupon.getProduct().getId().equals(reservation.getProduct().getId())) {
//                throw new BusinessException(COUPON_NOT_AVAILABLE);
//            }
//            if (coupon.getMinPurchaseAmount() != null
//                    && originalPrice < coupon.getMinPurchaseAmount().longValue()) {
//                throw new BusinessException(COUPON_NOT_AVAILABLE);
//            }
//
//            // 할인 계산
//            switch (coupon.getDiscountType()) {
//                case AMOUNT -> discount = coupon.getDiscountValue().longValue();
//                case RATE -> {
//                    long calc = Math.round(originalPrice * coupon.getDiscountValue().doubleValue() / 100.0);
//                    if (coupon.getMaxDiscountAmount() != null) {
//                        calc = Math.min(calc, coupon.getMaxDiscountAmount().longValue());
//                    }
//                    discount = calc;
//                }
//            }
//
//            // 쿠폰 사용 처리
//            customerCoupon.markAsUsed();
//        }

//        Long total = Math.max(0, originalPrice - discount);

        // 5. 디테일 생성
        OrderDetail detail = OrderDetail.of(reservation.getProduct(), null); //total);

        // 6. 주문 엔티티 생성
        Order order = Order.fromReservation(
                reservation,
                UUID.randomUUID().toString(),
                originalPrice,
                null,//discount,
                null,//total,
                List.of(detail)
        );

        Order saved = orderRepository.save(order);

        return OrderResponse.from(saved);
    }
}
