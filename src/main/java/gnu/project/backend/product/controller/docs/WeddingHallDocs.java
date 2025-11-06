package gnu.project.backend.product.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.WeddingHallRequest;
import gnu.project.backend.product.dto.request.WeddingHallUpdateRequest;
import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.WeddingHallTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "WeddingHall API", description = "웨딩홀 상품 CRUD 및 조회 API")

public interface WeddingHallDocs {

    @Operation(summary = "웨딩홀 생성", description = "오너 등록. request(JSON) + images(files[]) 멀티파트 업로드.")
    @ApiResponse(responseCode = "201", description = "생성 성공",
        content = @Content(schema = @Schema(implementation = WeddingHallResponse.class)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<WeddingHallResponse> createWeddingHall(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @RequestPart("request") WeddingHallRequest request,
        @RequestPart(name = "images", required = false) List<MultipartFile> images
    );

    @Operation(summary = "웨딩홀 수정", description = "이미지/옵션/태그/기본정보 갱신")
    @ApiResponse(responseCode = "200", description = "수정 성공",
        content = @Content(schema = @Schema(implementation = WeddingHallResponse.class)))
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<WeddingHallResponse> updateWeddingHall(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @PathVariable("id") Long id,
        @RequestPart("request") WeddingHallUpdateRequest request,
        @RequestPart(name = "images", required = false) List<MultipartFile> images
    );

    @Operation(summary = "웨딩홀 삭제", description = "소프트 삭제(isDeleted=true)")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteWeddingHall(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @PathVariable("id") Long id
    );

    @Operation(summary = "웨딩홀 상세 조회", description = "이미지/옵션/태그 포함 상세")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(schema = @Schema(implementation = WeddingHallResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<WeddingHallResponse> readWeddingHall(@PathVariable("id") Long id);


    @Operation(summary = "내 웨딩홀 목록", description = "오너 본인 소유 상품")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(schema = @Schema(implementation = Page.class)))
    @GetMapping("/me")
    ResponseEntity<Page<WeddingHallPageResponse>> readMyWeddingHalls(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
        @RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize
    );

    @Operation(
        summary = "웨딩홀"
            + ""
            + " 필터 조회",
        description = "태그, 카테고리, 지역, 가격 범위, 정렬 기준 등을 조합하여 드레스 목록을 필터링하여 조회한다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Page.class))
            )
        }
    )
    @GetMapping("/filter")
    ResponseEntity<Page<WeddingHallPageResponse>> getWeddingHallsByTags(
        @Parameter(
            description = "웨딩홀 태그 목록 (예: GENERAL, CONVENTION, HOTEL 등)",
            schema = @Schema(
                type = "string",
                allowableValues = {
                    "GENERAL",
                    "CONVENTION",
                    "HOTEL",
                    "HOUSE",
                    "RESTAURANT",
                    "HANOK",
                    "CHURCH",
                    "CHAPEL",
                    "SMALL",
                    "OUTDOOR_GARDEN",
                    "TRADITIONAL_WEDDING"
                }
            )
        )
        @RequestParam(required = false) List<WeddingHallTag> tags,

        @Parameter(
            description = "카테고리",
            required = false
        )
        @RequestParam(required = false) Category category,

        @Parameter(
            description = "지역 (예: SEOUL, BUSAN, DAEGU 등)",
            required = false
        )
        @RequestParam(required = false) Region region,

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
            schema = @Schema(
                type = "string",
                allowableValues = {"PRICE_ASC", "PRICE_DESC", "POPULAR", "LATEST"}
            )
        )
        @RequestParam(required = false, defaultValue = "LATEST") SortType sortType,

        @Parameter(
            description = "페이지 번호 (기본값: 1)",
            required = false
        )
        @RequestParam(required = false, defaultValue = "1") Integer pageNumber,

        @Parameter(
            description = "페이지 크기 (기본값: 6)",
            required = false
        )
        @RequestParam(required = false, defaultValue = "6") Integer pageSize
    );
}
