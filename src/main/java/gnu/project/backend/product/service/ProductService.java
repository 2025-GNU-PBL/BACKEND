package gnu.project.backend.product.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.error.ErrorCode;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OwnerRepository ownerRepository;

    public Page<ProductPageResponse> getMyProducts(
        final Accessor accessor,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                ErrorCode.IS_NOT_VALID_SOCIAL)
            );
        return productRepository.findProductsById(owner.getId(),
            pageNumber, pageSize);
    }
}
