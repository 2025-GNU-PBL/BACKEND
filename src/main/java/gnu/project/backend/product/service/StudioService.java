package gnu.project.backend.product.service;

import static gnu.project.backend.common.error.ErrorCode.MAKEUP_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.STUDIO_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.product.constant.ProductConstant.DRESS_DELETE_SUCCESS;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.dto.request.StudioRequest;
import gnu.project.backend.product.dto.request.StudioUpdateRequest;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import gnu.project.backend.product.entity.Studio;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.StudioTag;
import gnu.project.backend.product.helper.OwnerHelper;
import gnu.project.backend.product.helper.ProductHelper;
import gnu.project.backend.product.repository.StudioRepository;
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
public class StudioService {

    private final StudioRepository studioRepository;
    private final OwnerHelper ownerHelper;
    private final ProductHelper productHelper;


    @Transactional(readOnly = true)
    public StudioResponse read(
        final Long id
    ) {
        return studioRepository.findByStudioId(id);
    }

    @Transactional(readOnly = true)
    public Page<ProductPageResponse> readStudios(
        final Integer pageNumber,
        final Integer pageSize
    ) {
        long totalElements = studioRepository.count();
        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return new PageImpl<>(
            studioRepository.searchStudio(pageSize, pageNumber),
            pageable,
            totalElements
        );
    }

    public String delete(
        final Long id,
        final Accessor accessor
    ) {
        final Studio studio = studioRepository.findById(id)
            .orElseThrow(
                () -> new BusinessException(MAKEUP_NOT_FOUND_EXCEPTION)
            );

        ownerHelper.validateOwner(accessor, studio);
        studio.delete();

        return DRESS_DELETE_SUCCESS;
    }

    public StudioResponse update(
        final Long id,
        final StudioUpdateRequest request,
        final List<MultipartFile> images,
        final Accessor accessor,
        final List<Long> keepImagesId
    ) {
        final Studio studio = studioRepository.findStudioWithImagesAndOptionsById(id)
            .orElseThrow(
                () -> new BusinessException(STUDIO_NOT_FOUND_EXCEPTION)
            );

        ownerHelper.validateOwner(accessor, studio);

        productHelper.updateProductEnrichment(
            studio,
            images,
            keepImagesId,
            request.options(),
            request.tags()
        );

        studio.update(
            request.price(),
            request.address(),
            request.detail(),
            request.name(),
            request.availableTimes(),
            request.region()
        );
        return StudioResponse.from(studio);
    }

    public StudioResponse create(
        final StudioRequest request,
        final List<MultipartFile> images,
        final Accessor accessor
    ) {
        final Owner owner = ownerHelper.findOwnerBySocialId(accessor);
        final Studio savedStudio = studioRepository.save(
            Studio.create(
                owner,
                request.price(),
                request.address(),
                request.detail(),
                request.name(),
                request.availableTimes(),
                request.region()
            )
        );

        productHelper.createProduct(
            savedStudio,
            images,
            request.options(),
            request.tags()
        );
        return StudioResponse.from(savedStudio);
    }


    public Page<ProductPageResponse> getStudiosByFilters(List<StudioTag> tags, Category category,
        Region region, Integer minPrice, Integer maxPrice, SortType sortType,
        Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        List<ProductPageResponse> results = studioRepository.searchStudiosByFilter(
            tags, category, region, minPrice, maxPrice, sortType, pageNumber, pageSize
        );

        long total = studioRepository.countStudiosByFilter(tags, category, region, minPrice,
            maxPrice);

        return new PageImpl<>(results, pageable, total);
    }
}
