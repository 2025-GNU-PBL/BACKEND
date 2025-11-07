package gnu.project.backend.order.dto.request;

public record OrderCreateRequest(
        Long reservationId,
        Long userCouponId
) {
}
