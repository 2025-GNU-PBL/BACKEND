package gnu.project.backend.reservation.prefill.repository;

import gnu.project.backend.reservation.prefill.entity.ReservationPrefill;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationPrefillRepository
        extends JpaRepository<ReservationPrefill, Long>, ReservationPrefillCustomRepository {

    Optional<ReservationPrefill> findById(Long id);
}
