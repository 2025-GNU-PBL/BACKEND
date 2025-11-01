package gnu.project.backend.coupon.event.listener;

import gnu.project.backend.coupon.event.CouponExpiredEvent;
import gnu.project.backend.coupon.repository.CouponRepository;
import gnu.project.backend.coupon.repository.CustomerCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponExpirationListener {

    private final CouponRepository couponRepository;
    private final CustomerCouponRepository customerCouponRepository;

    @EventListener
    @Transactional
    public void handleCouponExpiration(CouponExpiredEvent event) {
        log.info("[CouponExpiredEvent] couponId={}, expiredAt={}", event.couponId(),
            event.expiredAt());

        couponRepository.findById(event.couponId()).ifPresent(coupon -> {
            coupon.expire();
            customerCouponRepository.deactivateAllByCouponId(coupon.getId());
            log.info("쿠폰 만료 처리 완료: {}", coupon.getId());
        });
    }
}
