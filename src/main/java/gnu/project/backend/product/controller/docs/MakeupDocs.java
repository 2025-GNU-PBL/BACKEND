package gnu.project.backend.product.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.MakeupRequest;
import gnu.project.backend.product.dto.request.MakeupUpdateRequest;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.MakeupTag;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Makeup API", description = "메이크업 상품 관련 API")
public interface MakeupDocs {

    @Operation(
        summary = "메이크업 상품 생성",
        description = "메이크업 상품을 생성하고 이미지를 업로드할 수 있습니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "메이크업 상품 생성 성공",
                content = @Content(schema = @Schema(implementation = MakeupResponse.class))
            )
        }
    )
    ResponseEntity<MakeupResponse> createMakeup(
        @Parameter(description = "메이크업 상품 요청 DTO", required = true)
        @RequestPart("request") MakeupRequest request,

        @Parameter(description = "업로드할 이미지 파일 목록", required = false)
        @RequestPart(value = "images", required = false) List<MultipartFile> images,

        @Auth Accessor accessor
    );

    @Operation(
        summary = "메이크업 상품 수정",
        description = "기존 메이크업 상품 정보를 수정하고 이미지를 변경할 수 있습니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "메이크업 상품 수정 성공",
                content = @Content(schema = @Schema(implementation = MakeupResponse.class))
            )
        }
    )
    ResponseEntity<MakeupResponse> updateMakeup(
        @Parameter(description = "수정할 상품 ID", required = true)
        @PathVariable(name = "id") Long id,

        @Parameter(description = "메이크업 상품 수정 요청 DTO", required = true)
        @RequestPart("request") MakeupUpdateRequest request,

        @Parameter(description = "업로드할 이미지 파일 목록", required = false)
        @RequestPart(value = "images", required = false) List<MultipartFile> images,

        @Auth Accessor accessor
    );

    @Operation(
        summary = "메이크업 상품 단건 조회",
        description = "메이크업 상품의 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = MakeupResponse.class))
            )
        }
    )
    ResponseEntity<MakeupResponse> readMakeup(
        @Parameter(description = "조회할 상품 ID", required = true)
        @PathVariable(name = "id") Long id
    );

    @Operation(
        summary = "메이크업 상품 목록 조회",
        description = "페이지네이션을 이용해 메이크업 상품 목록을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ProductPageResponse.class))
            )
        }
    )
    ResponseEntity<Page<ProductPageResponse>> readMakeups(
        @Parameter(description = "페이지 번호 (기본값 1)")
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,

        @Parameter(description = "페이지 크기 (기본값 6)")
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") Integer pageSize
    );

    @Operation(
        summary = "메이크업 상품 삭제",
        description = "특정 메이크업 상품을 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공")
        }
    )
    ResponseEntity<String> deleteMakeup(
        @Auth Accessor accessor,

        @Parameter(description = "삭제할 상품 ID", required = true)
        @PathVariable(name = "id") Long id
    );

    @Operation(
        summary = "메이크업 필터 조회",
        description = "태그, 지역, 가격, 정렬 기준 등을 조합하여 메이크업 목록을 필터링해 조회한다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Page.class))
            )
        }
    )
    @GetMapping("/filter")
    ResponseEntity<Page<ProductPageResponse>> getMakeupsByTags(

        @Parameter(
            description = "메이크업 태그 목록 (예: SHOOTING, CEREMONY, CLEAN_AND_BRIGHT 등)",
            schema = @Schema(
                type = "string",
                allowableValues = {
                    "SHOOTING_AND_CEREMONY",   // 촬영 + 본식 메이크업
                    "CEREMONY",                // 본식 메이크업
                    "SHOOTING",                // 촬영 메이크업
                    "DIRECTOR_OR_CEO",         // 원장 또는 대표 담당
                    "DEPUTY_DIRECTOR",         // 부원장 담당
                    "MANAGER",                 // 실장 담당
                    "TEAM_LEADER_OR_DESIGNER", // 팀장 또는 디자이너 담당
                    "FRUITY_TONE",             // 과즙톤 메이크업
                    "CLEAN_AND_BRIGHT",        // 깨끗하고 밝은 스타일
                    "CONTOUR_AND_SHADOW"       // 윤곽 및 음영 중심
                }
            )
        )
        @RequestParam(required = false) List<MakeupTag> tags,

        @Parameter(
            description = "카테고리 (예: MAKEUP, DRESS, WEDDING_HALL 등)",
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
