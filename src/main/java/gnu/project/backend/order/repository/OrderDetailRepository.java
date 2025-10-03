package gnu.project.backend.order.repository;

import gnu.project.backend.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository <OrderDetail, Long>{

}
