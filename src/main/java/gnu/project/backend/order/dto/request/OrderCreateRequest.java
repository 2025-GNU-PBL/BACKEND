package gnu.project.backend.order.dto.request;

public record OrderCreateRequest(
        Long reservationId,    // 어떤 예약으로부터 결제하는지
        Long userCouponId      // 내가 가진 customer_coupon.id (없으면 null)
) {
}
