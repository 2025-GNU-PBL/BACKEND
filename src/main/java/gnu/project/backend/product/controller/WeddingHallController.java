package gnu.project.backend.product.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.controller.docs.WeddingHallDocs;
import gnu.project.backend.product.dto.request.WeddingHallRequest;
import gnu.project.backend.product.dto.request.WeddingHallUpdateRequest;
import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.WeddingHallTag;
import gnu.project.backend.product.service.WeddingHallService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wedding-hall")
public class WeddingHallController implements WeddingHallDocs {

    private final WeddingHallService weddingHallService;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WeddingHallResponse> createWeddingHall(
            @Parameter(hidden = true) @Auth final Accessor accessor,
            @Valid @RequestPart("request") final WeddingHallRequest request,
            @RequestPart(name = "images", required = false) final List<MultipartFile> images
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(weddingHallService.create(request, images, accessor));
    }

    @Override
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WeddingHallResponse> updateWeddingHall(
            @Parameter(hidden = true) @Auth final Accessor accessor,
            @PathVariable("id") final Long id,
            @Valid @RequestPart("request") final WeddingHallUpdateRequest request,
            @RequestPart(name = "images", required = false) final List<MultipartFile> images
    ) {
        return ResponseEntity.ok(
                weddingHallService.update(id, request, images, request.keepImagesId(), accessor)
        );
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWeddingHall(
            @Parameter(hidden = true) @Auth final Accessor accessor,
            @PathVariable("id") final Long id
    ) {
        return ResponseEntity.ok(weddingHallService.delete(id, accessor));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<WeddingHallResponse> readWeddingHall(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(weddingHallService.read(id));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<Page<WeddingHallPageResponse>> readMyWeddingHalls(
            @Parameter(hidden = true) @Auth final Accessor accessor,
            @RequestParam(name = "pageNumber", defaultValue = "1") final Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(
                weddingHallService.readMyWeddingHalls(accessor, pageNumber, pageSize)
        );
    }

    @Override
    @GetMapping("/filter")
    public ResponseEntity<Page<WeddingHallPageResponse>> getWeddingHallsByTags(
            @RequestParam(required = false) final List<WeddingHallTag> tags,
            @RequestParam(required = false) final Category category,
            @RequestParam(required = false) final Region region,
            @RequestParam(required = false) final Integer minPrice,
            @RequestParam(required = false) final Integer maxPrice,
            @RequestParam(required = false, defaultValue = "LATEST") final SortType sortType,
            @RequestParam(required = false, defaultValue = "1") final Integer pageNumber,
            @RequestParam(required = false, defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(
                weddingHallService.getWeddingHallsByFilters(
                        tags, category, region, minPrice, maxPrice, sortType, pageNumber, pageSize
                )
        );
    }
}
