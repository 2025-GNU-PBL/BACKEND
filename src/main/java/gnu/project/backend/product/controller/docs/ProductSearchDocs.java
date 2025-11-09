package gnu.project.backend.product.controller.docs;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.dto.response.RecentSearchResponse;
import gnu.project.backend.product.enumerated.SortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@Tag(name = "Product Search", description = "상품 검색 및 최근 검색어 API")
public interface ProductSearchDocs {

    @Operation(
        summary = "상품 검색",
        description = "키워드와 정렬 방식에 따라 상품 목록을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "상품 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            )
        }
    )
    ResponseEntity<Page<ProductPageResponse>> searchAllProducts(
        @Parameter(description = "검색 키워드", required = true) String keyword,
        @Parameter(description = "정렬 방식", required = false) SortType sortType,
        @Parameter(description = "페이지 번호", required = false) Integer pageNumber,
        @Parameter(description = "페이지 크기", required = false) Integer pageSize
    );

    @Operation(
        summary = "최근 검색어 저장",
        description = "사용자가 검색한 키워드를 최근 검색어로 저장합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "최근 검색어 저장 및 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecentSearchResponse.class)
                )
            )
        }
    )
    ResponseEntity<RecentSearchResponse> saveSearchKeyword(
        @Parameter(description = "검색 키워드", required = true) String keyword,
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "최근 검색어 조회",
        description = "사용자의 최근 검색어 목록을 반환합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "최근 검색어 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecentSearchResponse.class)
                )
            )
        }
    )
    ResponseEntity<RecentSearchResponse> getRecentSearches(
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "최근 검색어 삭제",
        description = "특정 키워드를 최근 검색어 목록에서 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecentSearchResponse.class)
                ))

        }
    )
    ResponseEntity<RecentSearchResponse> deleteSearchKeyword(
        @Parameter(description = "삭제할 키워드", required = true) String keyword,
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "전체 최근 검색어 삭제",
        description = "사용자의 전체 최근 검색어를 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "전체 삭제 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecentSearchResponse.class)
                ))
        }
    )
    ResponseEntity<RecentSearchResponse> deleteAllSearches(
        @Parameter(hidden = true) Accessor accessor
    );
}
