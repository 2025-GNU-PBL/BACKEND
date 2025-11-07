package gnu.project.backend.product.controller.docs;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.DressRequest;
import gnu.project.backend.product.dto.request.DressUpdateRequest;
import gnu.project.backend.product.dto.response.DressResponse;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.DressTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Dress API", description = "드레스 상품 관련 API")
public interface DressDocs {

    @Operation(
        summary = "드레스 상품 등록",
        description = "드레스 상품을 등록합니다. 이미지 파일 업로드가 가능합니다."
    )
    @PostMapping()
    ResponseEntity<DressResponse> createDress(
        @Valid @RequestPart("request") DressRequest request,
        @Parameter(description = "업로드할 이미지 파일들 (선택)")
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "드레스 상품 수정",
        description = "기존 드레스 상품 정보를 수정합니다. 기존 이미지를 유지할 수 있으며, 새로운 이미지를 추가할 수 있습니다."
    )
    @PatchMapping("/{id}")
    ResponseEntity<DressResponse> updateDress(
        @Parameter(description = "수정할 드레스 ID") @PathVariable(name = "id") Long id,
        @Valid @RequestPart("request") DressUpdateRequest request,
        @Parameter(description = "업로드할 이미지 파일들 (선택)")
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "드레스 상품 단건 조회",
        description = "특정 드레스 상품의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    ResponseEntity<DressResponse> readDress(
        @Parameter(description = "조회할 드레스 ID") @PathVariable(name = "id") Long id
    );

    @Operation(
        summary = "드레스 상품 목록 조회",
        description = "드레스 상품 목록을 페이지네이션하여 조회합니다."
    )
    @GetMapping()
    ResponseEntity<Page<ProductPageResponse>> readDresses(
        @Parameter(description = "페이지 번호 (기본값: 1)")
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
        @Parameter(description = "페이지 사이즈 (기본값: 6)")
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") Integer pageSize
    );

    @Operation(
        summary = "드레스 상품 삭제",
        description = "드레스 상품을 삭제합니다."
    )
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteDress(
        @Parameter(hidden = true) Accessor accessor,
        @Parameter(description = "삭제할 드레스 ID") @PathVariable(name = "id") Long id
    );

    @Operation(
        summary = "드레스 필터 조회",
        description = "태그, 지역, 가격, 정렬 기준 등을 조합하여 드레스 목록을 필터링해 조회한다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Page.class)
                )
            )
        }
    )
    @GetMapping("/filter")
    ResponseEntity<Page<ProductPageResponse>> getDressesByTags(

        @Parameter(
            description = "드레스 태그 목록 (예: SILK, LACE, BEADS, DOMESTIC, IMPORTED 등)",
            schema = @io.swagger.v3.oas.annotations.media.Schema(
                type = "string",
                allowableValues = {
                    "SILK",                    // 실크 소재
                    "LACE",                    // 레이스 소재
                    "BEADS",                   // 비즈 장식
                    "DOMESTIC",                // 국내 드레스
                    "IMPORTED",                // 수입 드레스
                    "DOMESTIC_AND_IMPORTED",   // 국내 + 수입 병행
                    "SHOOTING",                // 촬영용 드레스
                    "CEREMONY",                // 본식용 드레스
                    "SHOOTING_AND_CEREMONY"    // 촬영 + 본식 겸용
                }
            )
        )
        @RequestParam(required = false) List<DressTag> tags,

        @Parameter(
            description = "카테고리 (예: DRESS, STUDIO, WEDDING_HALL 등)",
            required = false
        )
        @RequestParam(required = false) gnu.project.backend.product.enumerated.Category category,

        @Parameter(
            description = "지역 (예: SEOUL, BUSAN, DAEGU 등)",
            required = false
        )
        @RequestParam(required = false) gnu.project.backend.product.enumerated.Region region,

        @Parameter(
            description = "최소 가격 (원 단위)",
            required = false
        )
        @RequestParam(required = false) Integer minPrice,

        @Parameter(
            description = "최대 가격 (원 단위)",
            required = false
        )
        @RequestParam(required = false) Integer maxPrice,

        @Parameter(
            description = "정렬 기준",
            schema = @io.swagger.v3.oas.annotations.media.Schema(
                type = "string",
                allowableValues = {"PRICE_ASC", "PRICE_DESC", "POPULAR", "LATEST"}
            )
        )
        @RequestParam(required = false, defaultValue = "LATEST")
        gnu.project.backend.product.enumerated.SortType sortType,

        @Parameter(
            description = "페이지 번호 (기본값: 1)",
            required = false
        )
        @RequestParam(required = false, defaultValue = "1")
        Integer pageNumber,

        @Parameter(
            description = "페이지 크기 (기본값: 6)",
            required = false
        )
        @RequestParam(required = false, defaultValue = "6")
        Integer pageSize
    );
}
