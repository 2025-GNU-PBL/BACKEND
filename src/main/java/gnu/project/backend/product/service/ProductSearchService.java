package gnu.project.backend.product.service;

import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;


    public Page<ProductPageResponse> search(
        final String keyword,
        final Category category,
        final SortType sortType,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        return null;
    }
}
