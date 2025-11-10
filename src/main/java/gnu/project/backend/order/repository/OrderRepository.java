package gnu.project.backend.order.repository;

import gnu.project.backend.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {
    boolean existsByReservationId(Long reservationId);
}
