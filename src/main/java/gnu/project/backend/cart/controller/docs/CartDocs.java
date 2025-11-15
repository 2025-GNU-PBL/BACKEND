package gnu.project.backend.cart.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.cart.dto.request.CartAddRequest;
import gnu.project.backend.cart.dto.request.CartBulkDeleteRequest;
import gnu.project.backend.cart.dto.request.CartItemUpdateRequest;
import gnu.project.backend.cart.dto.response.CartSummaryResponse;
import gnu.project.backend.reservation.prefill.dto.response.CreateDraftsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Cart API", description = "장바구니 CRUD 및 체크아웃 API")
public interface CartDocs {

    @Operation(summary = "장바구니 담기", description = "상품을 장바구니에 추가한다.")
    @PostMapping("/api/v1/cart")
    ResponseEntity<Void> addItem(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Valid @RequestBody CartAddRequest request
    );

    @Operation(summary = "내 장바구니 조회", description = "장바구니 아이템과 금액 요약을 조회한다.")
    @GetMapping("/api/v1/cart")
    ResponseEntity<CartSummaryResponse> getMyCart(
        @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(summary = "장바구니 아이템 수정", description = "수량/선택여부를 수정한다.")
    @PatchMapping("/api/v1/cart/items/{cartItemId}")
    ResponseEntity<Void> updateItem(
        @PathVariable Long cartItemId,
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Valid @RequestBody CartItemUpdateRequest request
    );

    @Operation(summary = "장바구니 단건 삭제", description = "특정 아이템을 삭제한다.")
    @DeleteMapping("/api/v1/cart/items/{cartItemId}")
    ResponseEntity<Void> deleteItem(
        @PathVariable Long cartItemId,
        @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(summary = "장바구니 여러 건 삭제", description = "선택된 아이템 ID 목록으로 일괄 삭제한다.")
    @PostMapping("/api/v1/cart/items/bulk-delete")
    ResponseEntity<Void> bulkDelete(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Valid @RequestBody CartBulkDeleteRequest request
    );

    @Operation(summary = "전체 선택/해제", description = "내 장바구니 아이템 전체를 선택/해제한다.")
    @PostMapping("/api/v1/cart/select-all")
    ResponseEntity<Void> selectAll(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @RequestParam boolean selected
    );


    @Operation(
        summary = "선택 항목으로 문의 프리필 생성",
        description = """
            장바구니에서 selected=true인 아이템들로 30분 만료의 문의 프리필(draft)을 생성한다.
            반환된 draftIds를 이용해 /api/v1/inquiries/drafts/{id}로 프리필 조회 후
            문의 페이지 폼을 미리 채운다. 실제 예약 생성은 /api/v1/inquiries/from-draft에서 진행.
            """
    )
    @PostMapping("/api/v1/cart/checkout/inquiry-drafts")
    CreateDraftsResponse checkoutToInquiryDrafts(
        @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(
        summary = "내 장바구니 상품 개수 조회",
        description = "현재 로그인한 사용자의 장바구니에 담긴 상품 개수를 반환한다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "성공적으로 장바구니 개수를 조회함"
            )
        }
    )
    ResponseEntity<Integer> countMyCart(
        @Parameter(
            hidden = true,
            description = "로그인 사용자 정보"
        )
        Accessor accessor
    );
}


