package gnu.project.backend.product.repository;

import gnu.project.backend.product.entity.Product;
import java.util.Optional;

public interface ProductCustomRepository {

    Optional<Product> findByIdWithOwner(final Long id);
}
