package gnu.project.backend.order.service;

import gnu.project.backend.reservation.entity.Reservation;

public interface OrderCouponPolicy {
    long previewDiscount(Reservation reservation,
                         long originalPrice,
                         Long customerCouponId,
                         String customerSocialId);

    void markUsed(Long customerCouponId, String customerSocialId);
}
