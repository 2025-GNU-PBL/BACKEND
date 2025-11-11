package gnu.project.backend.reservation.prefill.repository;

import gnu.project.backend.reservation.prefill.entity.ReservationPrefill;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ReservationPrefillCustomRepository {

    Optional<ReservationPrefill> findActiveByIdAndCustomerId(Long id, Long customerId, LocalDateTime now);

}
