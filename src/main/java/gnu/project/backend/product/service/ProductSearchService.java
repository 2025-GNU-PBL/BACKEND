package gnu.project.backend.product.service;

import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;


    public Page<ProductPageResponse> search(
        final String keyword,
        final SortType sortType,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        return productSearchRepository.searchAll(
            keyword,
            sortType,
            pageNumber,
            pageSize
        );
    }
}
