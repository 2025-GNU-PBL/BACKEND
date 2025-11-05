package gnu.project.backend.product.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.controller.docs.StudioDocs;
import gnu.project.backend.product.dto.request.StudioRequest;
import gnu.project.backend.product.dto.request.StudioUpdateRequest;
import gnu.project.backend.product.dto.response.StudioPageResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.StudioTag;
import gnu.project.backend.product.service.StudioService;
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
@RequestMapping("/api/v1/studio")
public class StudioController implements StudioDocs {

    private final StudioService studioService;

    @Override
    @PostMapping
    public ResponseEntity<StudioResponse> createStudio(
        @Valid @RequestPart("request") final StudioRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(studioService.create(request, images, accessor));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<StudioResponse> updateStudio(
        @PathVariable(name = "id") final Long id,
        @Valid @RequestPart("request") final StudioUpdateRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(studioService.update(id, request, images, accessor, request.keepImagesId()));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<StudioResponse> readStudio(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(studioService.read(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<StudioPageResponse>> readStudios(
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(studioService.readStudios(pageNumber, pageSize));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudio(
        @Auth final Accessor accessor,
        @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(studioService.delete(id, accessor));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<StudioPageResponse>> getProductsByTags(
        @RequestParam(required = false) List<StudioTag> tags,
        @RequestParam(required = false) Category category,
        @RequestParam(required = false) Region region,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice,
        @RequestParam(required = false, defaultValue = "LATEST") SortType sortType,
        @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
        @RequestParam(required = false, defaultValue = "6") Integer pageSize
    ) {
        return ResponseEntity.ok(
            studioService.getStudiosByFilters(tags, category, region, minPrice, maxPrice, sortType,
                pageNumber, pageSize)
        );
    }
}
