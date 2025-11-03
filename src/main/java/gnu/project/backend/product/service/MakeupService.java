package gnu.project.backend.product.service;

import static gnu.project.backend.common.error.ErrorCode.MAKEUP_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.product.constant.ProductConstant.MAKEUP_DELETE_SUCCESS;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.dto.request.MakeupRequest;
import gnu.project.backend.product.dto.request.MakeupUpdateRequest;
import gnu.project.backend.product.dto.response.MakeupPageResponse;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.helper.ProductHelper;
import gnu.project.backend.product.repository.MakeupRepository;
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
@Transactional
public class MakeupService {

    private final MakeupRepository makeupRepository;
    private final OwnerRepository ownerRepository;
    private final ProductHelper productHelper;

    private static void validOwner(final Accessor accessor, final Makeup makeup) {
        if (!makeup.validOwner(accessor.getSocialId())) {
            throw new BusinessException(OWNER_NOT_FOUND_EXCEPTION);
        }
    }

    @Transactional(readOnly = true)
    public MakeupResponse read(
        final Long id
    ) {
        return makeupRepository.findByMakeupId(id);
    }

    @Transactional(readOnly = true)
    public Page<MakeupPageResponse> readMakeups(
        final Integer pageNumber,
        final Integer pageSize
    ) {
        long totalElements = makeupRepository.count();
        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return new PageImpl<>(
            makeupRepository.searchMakeup(pageSize, pageNumber),
            pageable,
            totalElements
        );
    }

    public String delete(
        final Long id,
        final Accessor accessor
    ) {
        final Makeup makeup = makeupRepository.findById(id)
            .orElseThrow(
                () -> new BusinessException(MAKEUP_NOT_FOUND_EXCEPTION)
            );

        validOwner(accessor, makeup);

        makeup.delete();

        return MAKEUP_DELETE_SUCCESS;
    }

    public MakeupResponse update(
        final Long id,
        final MakeupUpdateRequest request,
        final List<MultipartFile> images,
        final Accessor accessor,
        final List<Long> keepImagesId
    ) {
        final Makeup makeup = makeupRepository.findById(id)
            .orElseThrow(
                () -> new BusinessException(MAKEUP_NOT_FOUND_EXCEPTION)
            );

        validOwner(accessor, makeup);

        productHelper.updateProductEnrichment(
            makeup,
            images,
            keepImagesId,
            request.options(),
            request.tags()
        );

        makeup.update(
            request.price(),
            request.address(),
            request.detail(),
            request.name(),
            request.style(),
            request.availableTimes(),
            request.type(),
            request.region()
        );
        return MakeupResponse.from(makeup);
    }

    public MakeupResponse create(
        final MakeupRequest request,
        final List<MultipartFile> images,
        final Accessor accessor
    ) {
        final Owner owner = findOwnerBySocialId(accessor);
        final Makeup savedMakeup = makeupRepository.save(
            Makeup.create(
                owner,
                request.price(),
                request.address(),
                request.detail(),
                request.name(),
                request.style(),
                request.availableTimes(),
                request.type(),
                request.region()
            )
        );

        productHelper.createProduct(
            savedMakeup,
            images,
            request.options(),
            request.tags()
        );
        return MakeupResponse.from(savedMakeup);
    }


    private Owner findOwnerBySocialId(final Accessor accessor) {
        return ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                OWNER_NOT_FOUND_EXCEPTION)
            );
    }
}
