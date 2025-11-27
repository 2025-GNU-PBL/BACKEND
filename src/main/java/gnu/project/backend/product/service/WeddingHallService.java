package gnu.project.backend.product.service;

import static gnu.project.backend.common.error.ErrorCode.WEDDING_HALL_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.product.constant.ProductConstant.WEDDING_HALL_DELETE_SUCCESS;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.dto.request.WeddingHallRequest;
import gnu.project.backend.product.dto.request.WeddingHallUpdateRequest;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.WeddingHallTag;
import gnu.project.backend.product.helper.OwnerHelper;
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
@Transactional
@RequiredArgsConstructor
public class WeddingHallService {

    private final WeddingHallRepository weddingHallRepository;
    private final ProductHelper productHelper;
    private final OwnerHelper ownerHelper;


    @Transactional(readOnly = true)
    public WeddingHallResponse read(final Long id) {
        final WeddingHallResponse hall = weddingHallRepository.findByWeddingHallId(id);
        if (hall == null) {
            throw new BusinessException(WEDDING_HALL_NOT_FOUND_EXCEPTION);
        }
        return hall;
    }

    // --- my list (owner) : 최신순 & 페이지 (Repo에서 Page 반환) ---
    @Transactional(readOnly = true)
    public Page<ProductPageResponse> readMyWeddingHalls(
        final Accessor accessor,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return weddingHallRepository.searchWeddingHallByOwner(accessor.getSocialId(), pageable);
    }

    public WeddingHallResponse create(
        final WeddingHallRequest request,
        final List<MultipartFile> images,
        final Accessor accessor
    ) {
        final Owner owner = ownerHelper.findOwnerBySocialId(accessor);

        final WeddingHall hall = WeddingHall.create(
            owner,
            request.price(),
            request.address(),
            request.detail(),
            request.name(),
            request.capacity(),
            request.minGuest(),
            request.maxGuest(),
            request.parkingCapacity(),
            request.cateringType(),
            request.availableTimes(),
            request.reservationPolicy(),
            request.region()
        );

        final WeddingHall saved = weddingHallRepository.save(hall);

        productHelper.createProduct(
            saved,
            images,
            request.options(),
            request.tags()
        );

        return WeddingHallResponse.from(saved);
    }

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

        ownerHelper.validateOwner(accessor, hall);
        List<Image> existingImages = hall.getImages();

        productHelper.updateProductEnrichment(
            hall,
            existingImages,
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
            request.parkingCapacity(),
            request.cateringType(),
            request.availableTimes(),
            request.reservationPolicy(),
            request.region()
        );

        return WeddingHallResponse.from(hall);
    }

    public String delete(final Long id, final Accessor accessor) {
        final WeddingHall hall = weddingHallRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(WEDDING_HALL_NOT_FOUND_EXCEPTION));

        ownerHelper.validateOwner(accessor, hall);
        hall.delete();
        return WEDDING_HALL_DELETE_SUCCESS;
    }

    @Transactional(readOnly = true)
    public Page<ProductPageResponse> getWeddingHallsByFilters(
        final List<WeddingHallTag> tags,
        final Category category,
        final Region region,
        final Integer minPrice,
        final Integer maxPrice,
        final SortType sortType,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        final List<ProductPageResponse> rows =
            weddingHallRepository.searchWeddingHallByFilter(
                tags, category, region, minPrice, maxPrice, sortType, pageNumber, pageSize
            );

        final long total =
            weddingHallRepository.countWeddingHallByFilter(
                tags, category, region, minPrice, maxPrice
            );

        return new PageImpl<>(rows, pageable, total);
    }
}
