package gnu.project.backend.product.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.controller.docs.WeddingHallDocs;
import gnu.project.backend.product.dto.request.WeddingHallRequest;
import gnu.project.backend.product.dto.request.WeddingHallUpdateRequest;
import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.service.WeddingHallService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
            .body(
                weddingHallService.create(
                    request,
                    images,
                    accessor
                )
            );
    }

    @Override
    @PatchMapping(
        value = "/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<WeddingHallResponse> updateWeddingHall(
        @Parameter(hidden = true) @Auth final Accessor accessor,
        @PathVariable("id") final Long id,
        @Valid @RequestPart("request") final WeddingHallUpdateRequest request,
        @RequestPart(name = "images", required = false) final List<MultipartFile> images
    ) {
        return ResponseEntity
            .ok(
                weddingHallService.update(
                    id,
                    request,
                    images,
                    request.keepImagesId(), // 기존 이미지 유지 목록
                    accessor
                )
            );
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWeddingHall(
        @Parameter(hidden = true) @Auth final Accessor accessor,
        @PathVariable("id") final Long id
    ) {
        return ResponseEntity.ok(
            weddingHallService.delete(id, accessor)
        );
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<WeddingHallResponse> readWeddingHall(
        @PathVariable("id") final Long id
    ) {
        return ResponseEntity.ok(
            weddingHallService.read(id)
        );
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<WeddingHallPageResponse>> readWeddingHalls(
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") final Integer pageSize,
        @RequestParam(name = "region", required = false) final Region region
    ) {
        return ResponseEntity.ok(
            weddingHallService.readWeddingHalls(pageNumber, pageSize, region)
        );
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<Page<WeddingHallPageResponse>> readMyWeddingHalls(
        @Parameter(hidden = true) @Auth final Accessor accessor,
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(
            weddingHallService.readMyWeddingHalls(accessor, pageNumber, pageSize)
        );
    }

//    @GetMapping("/filter")
//    public ResponseEntity<Page<DressPageResponse>> getProductsByTags(
//        @RequestParam(required = false) List<DressTag> tags,
//        @RequestParam(required = false) Category category,
//        @RequestParam(required = false) Region region,
//        @RequestParam(required = false) Integer minPrice,
//        @RequestParam(required = false) Integer maxPrice,
//        @RequestParam(required = false, defaultValue = "LATEST") SortType sortType,
//        @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
//        @RequestParam(required = false, defaultValue = "6") Integer pageSize
//    ) {
//        return ResponseEntity.ok(
//            dressService.getDressesByFilters(tags, category, region, minPrice, maxPrice, sortType,
//                pageNumber, pageSize)
//        );
//    }
}
