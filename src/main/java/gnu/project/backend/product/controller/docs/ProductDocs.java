package gnu.project.backend.product.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@Tag(name = "Owner Product", description = "업주(OWNER) 전용 상품 관리 API")
public interface ProductDocs {

    @Operation(
        summary = "내 상품 목록 조회",
        description = """
            로그인한 업주(OWNER)가 등록한 상품 목록을 페이지네이션으로 조회합니다.
            - `pageNumber`: 1부터 시작 (기본값: 1)
            - `pageSize`: 한 페이지당 상품 수 (기본값: 6)
            - `@OnlyOwner` 어노테이션으로 OWNER 권한 검증
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "내 상품 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Page.class)
        )
    )
    @ApiResponse(
        responseCode = "401",
        description = "인증되지 않음 (토큰 없음 또는 만료)",
        content = @Content
    )
    @ApiResponse(
        responseCode = "403",
        description = "권한 없음 (OWNER가 아님)",
        content = @Content
    )
    ResponseEntity<Page<ProductPageResponse>> getMyProducts(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        Integer pageNumber,
        @Parameter(description = "페이지 크기", example = "6")
        Integer pageSize
    );
}