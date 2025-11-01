package gnu.project.backend.coupon.service;

import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.event.CouponExpiredEvent;
import gnu.project.backend.coupon.repository.CouponRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponExpirationScheduler {

    private final CouponRepository couponRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 * * * *") // 매 시간 정각마다 실행
    @Transactional(readOnly = true)
    public void publishExpiredCoupons() {
        List<Coupon> expiredCoupons = couponRepository.findAllExpired(LocalDate.now());

        expiredCoupons.forEach(coupon -> {
            eventPublisher.publishEvent(CouponExpiredEvent.from(coupon));
        });
    }
}
