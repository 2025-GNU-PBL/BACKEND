package gnu.project.backend.product.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.helper.OwnerHelper;
import gnu.project.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OwnerHelper ownerHelper;

    public Page<ProductPageResponse> getMyProducts(
        final Accessor accessor,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        return productRepository.findProductsById(
            ownerHelper.findOwnerBySocialId(accessor).getId(),
            pageNumber,
            pageSize
        );
    }
}
