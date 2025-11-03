package gnu.project.backend.product.service;

import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.WEDDING_HALL_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.product.constant.ProductConstant.WEDDING_HALL_DELETE_SUCCESS;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.dto.request.WeddingHallRequest;
import gnu.project.backend.product.dto.request.WeddingHallUpdateRequest;
import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.helper.ProductHelper;
import gnu.project.backend.product.repository.WeddingHallRepository;
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
public class WeddingHallService {

    private final WeddingHallRepository weddingHallRepository;
    private final OwnerRepository ownerRepository;
    private final ProductHelper productHelper;

    private static void validateOwner(final Accessor accessor, final WeddingHall hall) {
        if (!hall.validOwner(accessor.getSocialId())) {
            throw new BusinessException(OWNER_NOT_FOUND_EXCEPTION);
        }
    }

    private Owner findOwnerBySocialId(final Accessor accessor) {
        return ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));
    }


    @Transactional(readOnly = true)
    public WeddingHallResponse read(final Long id) {
        final WeddingHallResponse hall = weddingHallRepository.findByWeddingHallId(id);
        if (hall == null) {
            throw new BusinessException(WEDDING_HALL_NOT_FOUND_EXCEPTION);
        }
        return hall;
    }

    @Transactional(readOnly = true)
    public Page<WeddingHallPageResponse> readWeddingHalls(
        final Integer pageNumber,
        final Integer pageSize,
        final Region region
    ) {
        final long totalElements = weddingHallRepository.countActiveByRegion(region);

        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        final List<WeddingHallPageResponse> pageContent =
            weddingHallRepository.searchWeddingHall(pageSize, pageNumber, region);

        return new PageImpl<>(
            pageContent,
            pageable,
            totalElements
        );
    }


    @Transactional(readOnly = true)
    public Page<WeddingHallPageResponse> readMyWeddingHalls(
        final Accessor accessor,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        final String socialId = accessor.getSocialId();

        final long totalElements = weddingHallRepository.countActiveByOwner(socialId);

        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        final List<WeddingHallPageResponse> pageContent =
            weddingHallRepository.searchWeddingHallByOwner(
                socialId,
                pageSize,
                pageNumber
            );

        return new PageImpl<>(
            pageContent,
            pageable,
            totalElements
        );
    }


    @Transactional
    public WeddingHallResponse create(
        final WeddingHallRequest request,
        final List<MultipartFile> images,
        final Accessor accessor
    ) {
        // 1) 현재 로그인한 Owner를 가져온다
        final Owner owner = findOwnerBySocialId(accessor);

        // 2) 엔티티 생성
        final WeddingHall hall = WeddingHall.create(
            owner,
            request.price(),
            request.address(),
            request.detail(),
            request.name(),
            request.capacity(),
            request.minGuest(),
            request.maxGuest(),
            request.hallType(),
            request.parkingCapacity(),
            request.cateringType(),
            request.availableTimes(),
            request.reservationPolicy(),
            request.region()
        );

        final WeddingHall saved = weddingHallRepository.save(hall);

        productHelper.createProduct(
            hall,
            images,
            request.options(),
            request.tags()
        );

        return WeddingHallResponse.from(saved);
    }


    @Transactional
    public WeddingHallResponse update(
        final Long id,
        final WeddingHallUpdateRequest request,
        final List<MultipartFile> newImages,
        final List<Long> keepImagesId,
        final Accessor accessor
    ) {

        final WeddingHall hall = weddingHallRepository
            .findWeddingHallWithImagesAndOptionsById(id)
            .orElseThrow(() -> new BusinessException(WEDDING_HALL_NOT_FOUND_EXCEPTION));

        validateOwner(accessor, hall);

        productHelper.updateProductEnrichment(
            hall,
            newImages,
            keepImagesId,
            request.options(),
            request.tags()
        );

        hall.update(
            request.price(),
            request.address(),
            request.detail(),
            request.name(),
            request.capacity(),
            request.minGuest(),
            request.maxGuest(),
            request.hallType(),
            request.parkingCapacity(),
            request.cateringType(),
            request.availableTimes(),
            request.reservationPolicy(),
            request.region()
        );

        return WeddingHallResponse.from(hall);
    }


    @Transactional
    public String delete(
        final Long id,
        final Accessor accessor
    ) {
        final WeddingHall hall = weddingHallRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(WEDDING_HALL_NOT_FOUND_EXCEPTION));

        validateOwner(accessor, hall);

        hall.delete();

        return WEDDING_HALL_DELETE_SUCCESS;
    }
}
