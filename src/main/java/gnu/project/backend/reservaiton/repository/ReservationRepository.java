package gnu.project.backend.reservaiton.repository;

import gnu.project.backend.reservaiton.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>,
    ReservationCustomRepository {

}
