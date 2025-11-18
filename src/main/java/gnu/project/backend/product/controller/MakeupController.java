package gnu.project.backend.product.controller;

import static gnu.project.backend.product.constant.PageConstant.DEFAULT_PAGE_NUMBER;
import static gnu.project.backend.product.constant.PageConstant.DEFAULT_PAGE_SIZE;
import static gnu.project.backend.product.constant.ProductConstant.DEFAULT_SORT_TYPE;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.aop.OnlyOwner;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.controller.docs.MakeupDocs;
import gnu.project.backend.product.dto.request.MakeupRequest;
import gnu.project.backend.product.dto.request.MakeupUpdateRequest;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.MakeupTag;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.service.MakeupService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/makeup")
public class MakeupController implements MakeupDocs {

    private final MakeupService makeupService;

    @Override
    @OnlyOwner
    @PostMapping
    public ResponseEntity<MakeupResponse> createMakeup(
        @Valid @RequestPart("request") final MakeupRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(makeupService.create(request, images, accessor));
    }

    @Override
    @OnlyOwner
    @PatchMapping("/{id}")
    public ResponseEntity<MakeupResponse> updateMakeup(
        @PathVariable(name = "id") final Long id,
        @Valid @RequestPart("request") final MakeupUpdateRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(makeupService.update(id, request, images, accessor, request.keepImagesId()));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MakeupResponse> readMakeup(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(makeupService.read(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<ProductPageResponse>> readMakeups(
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer pageNumber,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize
    ) {
        return ResponseEntity.ok(makeupService.readMakeups(pageNumber, pageSize));
    }

    @Override
    @OnlyOwner
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMakeup(
        @Auth final Accessor accessor,
        @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(makeupService.delete(id, accessor));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductPageResponse>> getMakeupsByTags(
        @RequestParam(required = false) List<MakeupTag> tags,
        @RequestParam(required = false) Category category,
        @RequestParam(required = false) Region region,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice,
        @RequestParam(required = false, defaultValue = DEFAULT_SORT_TYPE) SortType sortType,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer pageNumber,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize
    ) {
        return ResponseEntity.ok(
            makeupService.getMakeupsByFilters(tags, category, region, minPrice, maxPrice, sortType,
                pageNumber, pageSize)
        );
    }
}
