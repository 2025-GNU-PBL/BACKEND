package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.SortType;
import org.springframework.data.domain.Page;

public interface ProductSearchCustomRepository {

    Page<ProductPageResponse> searchAll(
        final String keyword,
        final SortType sortType,
        final int pageNumber,
        final int pageSize
    );

}
