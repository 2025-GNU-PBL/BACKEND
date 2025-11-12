package gnu.project.backend.reservation.prefill.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.reservation.prefill.entity.QReservationPrefill;
import gnu.project.backend.reservation.prefill.entity.ReservationPrefill;
import gnu.project.backend.reservation.prefill.repository.ReservationPrefillCustomRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationPrefillRepositoryImpl implements ReservationPrefillCustomRepository {

    private static final QReservationPrefill prefill = QReservationPrefill.reservationPrefill;
    private final JPAQueryFactory query;

    @Override
    public Optional<ReservationPrefill> findActiveByIdAndCustomerId(Long id, Long customerId,
        LocalDateTime now) {
        ReservationPrefill hit = query
            .selectFrom(prefill)
            .where(
                prefill.id.eq(id),
                prefill.customer.id.eq(customerId),
                prefill.consumed.isFalse(),
                prefill.expiresAt.gt(now)
            )
            .fetchFirst();
        return Optional.ofNullable(hit);
    }

    @Override
    public List<ReservationPrefill> findAllActiveByIdsAndCustomerId(List<Long> ids, Long customerId,
        LocalDateTime now) {
        return query
            .selectFrom(prefill)
            .where(
                prefill.id.in(ids),
                prefill.customer.id.eq(customerId),
                prefill.consumed.isFalse(),
                prefill.expiresAt.gt(now)
            )
            .fetch();
    }
}
