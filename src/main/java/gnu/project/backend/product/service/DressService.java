package gnu.project.backend.product.service;

import static gnu.project.backend.common.error.ErrorCode.DRESS_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.MAKEUP_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.product.constant.ProductConstant.DRESS_DELETE_SUCCESS;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.dto.request.DressRequest;
import gnu.project.backend.product.dto.request.DressUpdateRequest;
import gnu.project.backend.product.dto.response.DressPageResponse;
import gnu.project.backend.product.dto.response.DressResponse;
import gnu.project.backend.product.entity.Dress;
import gnu.project.backend.product.helper.ProductHelper;
import gnu.project.backend.product.repository.DressRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DressService {

    private final DressRepository dressRepository;
    private final OwnerRepository ownerRepository;
    private final ProductHelper productHelper;

    private static void validOwner(Accessor accessor, Dress dress) {
        if (!dress.getOwner().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(OWNER_NOT_FOUND_EXCEPTION);
        }
    }

    @Transactional(readOnly = true)
    public DressResponse read(
        final Long id
    ) {
        return dressRepository.findByDressId(id);
    }

    @Transactional(readOnly = true)
    public Page<DressPageResponse> readDresses(
        final Integer pageNumber,
        final Integer pageSize
    ) {
        long totalElements = dressRepository.count();
        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return new PageImpl<>(
            dressRepository.searchDress(pageSize, pageNumber),
            pageable,
            totalElements
        );
    }

    public String delete(
        final Long id,
        final Accessor accessor
    ) {
        final Dress dress = dressRepository.findById(id)
            .orElseThrow(
                () -> new BusinessException(MAKEUP_NOT_FOUND_EXCEPTION)
            );

        validOwner(accessor, dress);
        dress.delete();

        return DRESS_DELETE_SUCCESS;
    }

    public DressResponse update(
        final Long id,
        final DressUpdateRequest request,
        final List<MultipartFile> images,
        final Accessor accessor,
        final List<Long> keepImagesId
    ) {
        final Dress dress = dressRepository.findDressWithImagesAndOptionsById(id)
            .orElseThrow(
                () -> new BusinessException(DRESS_NOT_FOUND_EXCEPTION)
            );

        validOwner(accessor, dress);

        productHelper.updateProductEnrichment(
            dress,
            images,
            keepImagesId,
            request.options(),
            request.tags()
        );

        dress.update(
            request.price(),
            request.address(),
            request.detail(),
            request.name(),
            request.availableTimes()
        );
        return DressResponse.from(dress);
    }

    public DressResponse create(
        final DressRequest request,
        final List<MultipartFile> images,
        final Accessor accessor
    ) {
        final Owner owner = findOwnerBySocialId(accessor);
        final Dress savedDress = dressRepository.save(
            Dress.create(
                owner,
                request.price(),
                request.address(),
                request.detail(),
                request.name(),
                request.availableTimes()
            )
        );

        productHelper.createProduct(
            savedDress,
            images,
            request.options(),
            request.tags()
        );

        return DressResponse.from(savedDress);
    }


    private Owner findOwnerBySocialId(final Accessor accessor) {
        return ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                OWNER_NOT_FOUND_EXCEPTION)
            );
    }
}
