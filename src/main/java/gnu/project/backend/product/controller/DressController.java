package gnu.project.backend.product.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.DressRequest;
import gnu.project.backend.product.dto.request.DressUpdateRequest;
import gnu.project.backend.product.dto.response.DressPageResponse;
import gnu.project.backend.product.dto.response.DressResponse;
import gnu.project.backend.product.service.DressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/dress")
@Tag(name = "Dress API", description = "드레스 상품 관련 API")
public class DressController {

    private final DressService dressService;

    @PostMapping()
    @Operation(
        summary = "드레스 상품 등록",
        description = "드레스 상품을 등록합니다. 이미지 파일 업로드가 가능합니다."
    )
    public ResponseEntity<DressResponse> createDress(
        @Valid @RequestPart("request") final DressRequest request,
        @Parameter(description = "업로드할 이미지 파일들 (선택)")
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Parameter(hidden = true) @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                dressService.create(
                    request, images, accessor
                )
            );
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "드레스 상품 수정",
        description = "기존 드레스 상품 정보를 수정합니다. 기존 이미지를 유지할 수 있으며, 새로운 이미지를 추가할 수 있습니다."
    )
    public ResponseEntity<DressResponse> updateDress(
        @Parameter(description = "수정할 드레스 ID") @PathVariable(name = "id") final Long id,
        @Valid @RequestPart("request") final DressUpdateRequest request,
        @Parameter(description = "업로드할 이미지 파일들 (선택)")
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Parameter(hidden = true) @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                dressService.update(
                    id, request, images, accessor, request.keepImagesId()
                )
            );
    }


    @GetMapping("/{id}")
    @Operation(
        summary = "드레스 상품 단건 조회",
        description = "특정 드레스 상품의 상세 정보를 조회합니다."
    )
    public ResponseEntity<DressResponse> readDress(
        @Parameter(description = "조회할 드레스 ID") @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(dressService.read(id));
    }

    @GetMapping()
    @Operation(
        summary = "드레스 상품 목록 조회",
        description = "드레스 상품 목록을 페이지네이션하여 조회합니다."
    )
    public ResponseEntity<Page<DressPageResponse>> readDresses(
        @Parameter(description = "페이지 번호 (기본값: 1)")
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
        @Parameter(description = "페이지 사이즈 (기본값: 6)")
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(
            dressService.readDresses(pageNumber, pageSize)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "드레스 상품 삭제",
        description = "드레스 상품을 삭제합니다."
    )
    public ResponseEntity<String> deleteDress(
        @Parameter(hidden = true) @Auth final Accessor accessor,
        @Parameter(description = "삭제할 드레스 ID") @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(dressService.delete(id, accessor));
    }


}
