package gnu.project.backend.reservation.prefill.repository;

import gnu.project.backend.reservation.prefill.entity.ReservationPrefill;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationPrefillRepository
        extends JpaRepository<ReservationPrefill, Long>, ReservationPrefillCustomRepository {

    // 일반 JPA 메서드는 최소화 (핵심 조회는 커스텀으로 통일)
    Optional<ReservationPrefill> findById(Long id);
}
