package gnu.project.backend.reservaiton.repository;

import gnu.project.backend.reservaiton.entity.Reservation;
import java.util.Optional;

public interface ReservationCustomRepository {

    Optional<Reservation> findByIdWithOwner(final Long id);
}
