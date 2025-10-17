package gnu.project.backend.reservaiton.repository.impl;

import static gnu.project.backend.owner.entity.QOwner.owner;
import static gnu.project.backend.reservaiton.entity.QReservation.reservation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.reservaiton.entity.Reservation;
import gnu.project.backend.reservaiton.repository.ReservationCustomRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public Optional<Reservation> findByIdWithOwner(Long id) {
        return Optional.ofNullable(
            query
                .selectFrom(reservation)
                .leftJoin(owner)
                .fetchJoin()
                .where(reservation.id.eq(id))
                .fetchFirst()
        );
    }
}
