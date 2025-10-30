package gnu.project.backend.coupon.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.coupon.repository.CouponCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponCustomRepository {

    private final JPAQueryFactory query;

}
