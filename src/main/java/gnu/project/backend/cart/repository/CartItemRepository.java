package gnu.project.backend.cart.repository;

import gnu.project.backend.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemCustomRepository {
}
