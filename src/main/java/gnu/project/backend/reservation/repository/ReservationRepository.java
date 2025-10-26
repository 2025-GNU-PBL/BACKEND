package gnu.project.backend.reservation.repository;

import gnu.project.backend.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>,
    ReservationCustomRepository {

}
