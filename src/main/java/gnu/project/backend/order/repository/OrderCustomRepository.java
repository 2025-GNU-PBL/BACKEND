package gnu.project.backend.order.repository;

import gnu.project.backend.order.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderCustomRepository {

    List<Order> findAllByCustomerSocialId(String socialId);

    Optional<Order> findByOrderCodeWithDetails(String orderCode);

    boolean existsPaidByCustomerAndProduct(Long customerId, Long productId);
}
