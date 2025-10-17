package gnu.project.backend.reservaiton.repository.impl;

import static gnu.project.backend.customer.entity.QCustomer.customer;
import static gnu.project.backend.owner.entity.QOwner.owner;
import static gnu.project.backend.reservaiton.entity.QReservation.reservation;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.reservaiton.entity.Reservation;
import gnu.project.backend.reservaiton.repository.ReservationCustomRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationCustomRepository {

    private static final OrderSpecifier<?>[] RESERVATION_DEFAULT_ORDER = {
        reservation.createdAt.desc(),
        reservation.id.desc()
    };
    private final JPAQueryFactory query;

    @Override
    public Optional<Reservation> findReservationByIdWithOwner(final Long id) {
        return Optional.ofNullable(
            query
                .selectFrom(reservation)
                .leftJoin(owner)
                .fetchJoin()
                .where(reservation.id.eq(id))
                .fetchFirst()
        );
    }

    @Override
    public List<Reservation> findReservationsByOwnerId(final Long id) {
        return query
            .selectFrom(reservation)
            .leftJoin(owner)
            .fetchJoin()
            .where(reservation.owner.id.eq(id))
            .orderBy(RESERVATION_DEFAULT_ORDER)
            .fetch();
    }

    @Override
    public List<Reservation> findReservationsByCustomerId(final Long id) {
        return query
            .selectFrom(reservation)
            .leftJoin(customer)
            .fetchJoin()
            .where(reservation.customer.id.eq(id))
            .orderBy(RESERVATION_DEFAULT_ORDER)
            .fetch();
    }
}
