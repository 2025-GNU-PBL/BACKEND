package gnu.project.backend.cart.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.cart.controller.docs.CartDocs;
import gnu.project.backend.cart.dto.request.CartAddRequest;
import gnu.project.backend.cart.dto.request.CartBulkDeleteRequest;
import gnu.project.backend.cart.dto.request.CartItemUpdateRequest;
import gnu.project.backend.cart.dto.response.CartSummaryResponse;
import gnu.project.backend.cart.service.CartService;
import gnu.project.backend.reservation.prefill.dto.response.CreateDraftsResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController implements CartDocs {

    private final CartService cartService;

    @Override
    @PostMapping
    public ResponseEntity<Void> addItem(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Valid @RequestBody CartAddRequest request
    ) {
        cartService.addItem(accessor, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping
    public ResponseEntity<CartSummaryResponse> getMyCart(
        @Parameter(hidden = true) @Auth Accessor accessor
    ) {
        return ResponseEntity.ok(cartService.readMyCart(accessor));
    }

    @Override
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateItem(
        @PathVariable Long cartItemId,
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Valid @RequestBody CartItemUpdateRequest request
    ) {
        cartService.updateCartItem(cartItemId, accessor, request);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteItem(
        @PathVariable Long cartItemId,
        @Parameter(hidden = true) @Auth Accessor accessor
    ) {
        cartService.deleteCartItem(cartItemId, accessor);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/items/bulk-delete")
    public ResponseEntity<Void> bulkDelete(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Valid @RequestBody CartBulkDeleteRequest request
    ) {
        cartService.bulkDelete(request, accessor);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/select-all")
    public ResponseEntity<Void> selectAll(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @RequestParam boolean selected
    ) {
        cartService.toggleSelectAll(accessor, selected);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/checkout/inquiry-drafts")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateDraftsResponse checkoutToInquiryDrafts(
        @Parameter(hidden = true) @Auth Accessor accessor
    ) {
        return cartService.createInquiryDraftsFromSelected(accessor);
    }

    @Override
    @GetMapping("count")
    public ResponseEntity<Integer> countMyCart(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(cartService.getCartCount(accessor));
    }

}
