package gnu.project.backend.cart.repository;

import gnu.project.backend.cart.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomer_Id(Long customerId);

}
