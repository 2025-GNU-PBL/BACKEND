package gnu.project.backend.product.helper;

import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerHelper {

    private final OwnerRepository ownerRepository;

    public void validateOwner(final Accessor accessor, final Product product) {
        if (!product.validOwner(accessor.getSocialId())) {
            throw new BusinessException(OWNER_NOT_FOUND_EXCEPTION);
        }
    }

    public void validateOwnerById(final String socialId, final Product product) {
        if (!product.validOwner(socialId)) {
            throw new BusinessException(OWNER_NOT_FOUND_EXCEPTION);
        }
    }

    public Owner findOwnerBySocialId(final Accessor accessor) {
        return ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                OWNER_NOT_FOUND_EXCEPTION)
            );
    }

}
