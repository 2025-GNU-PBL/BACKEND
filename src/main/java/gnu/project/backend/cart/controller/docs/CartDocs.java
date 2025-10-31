package gnu.project.backend.cart.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.cart.dto.request.CartAddRequest;
import gnu.project.backend.cart.dto.request.CartBulkDeleteRequest;
import gnu.project.backend.cart.dto.request.CartItemUpdateRequest;
import gnu.project.backend.cart.dto.response.CartSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart API", description = "장바구니 CRUD 및 체크아웃 API")
public interface CartDocs {

    @Operation(summary = "장바구니 담기", description = "상품(+옵션)을 장바구니에 추가한다.")
    @PostMapping("/api/v1/cart")
    ResponseEntity<Void> addItem(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestBody CartAddRequest request
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
            @RequestBody CartItemUpdateRequest request
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
            @RequestBody CartBulkDeleteRequest request
    );

    @Operation(summary = "전체 선택/해제", description = "내 장바구니 아이템 전체를 선택/해제한다. selected=true/false")
    @PostMapping("/api/v1/cart/select-all")
    ResponseEntity<Void> selectAll(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestParam boolean selected
    );


    @Operation(
            summary = "선택 아이템으로 예약 생성",
            description = """
            선택된(selected=true) 장바구니 아이템들로 예약을 생성한다. 
            생성된 예약은 PENDING 상태로 저장되며, 성공 시 장바구니에서 해당 아이템은 제거된다.
            """
    )

    @PostMapping("/api/v1/cart/checkout/reservations")
    ResponseEntity<Void> checkoutToReservations(
            @Parameter(hidden = true) @Auth Accessor accessor
    );
}
