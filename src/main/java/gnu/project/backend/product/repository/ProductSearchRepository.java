package gnu.project.backend.product.repository;

import gnu.project.backend.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSearchRepository extends JpaRepository<Product, Long>,
    ProductSearchCustomRepository {


}
