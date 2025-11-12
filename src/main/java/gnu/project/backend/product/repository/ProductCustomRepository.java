package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProductCustomRepository {

    Optional<Product> findByIdWithOwner(final Long id);

    Page<ProductPageResponse> findProductsById(
        final Long id,
        final int pageNumber,
        final int pageSize
    );
}
