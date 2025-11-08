package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.SortType;
import java.util.List;

public interface ProductSearchCustomRepository {

    List<ProductPageResponse> searchAll(
        final String keyword,
        final SortType sortType,
        final int pageSize,
        final int pageNumber
    );

}
