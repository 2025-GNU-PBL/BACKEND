package gnu.project.backend.cart.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.cart.controller.docs.CartDocs;
import gnu.project.backend.cart.dto.request.CartAddRequest;
import gnu.project.backend.cart.dto.request.CartBulkDeleteRequest;
import gnu.project.backend.cart.dto.request.CartItemUpdateRequest;
import gnu.project.backend.cart.dto.response.CartSummaryResponse;
import gnu.project.backend.cart.service.CartService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController implements CartDocs {

    private final CartService cartService;

    @Override
    @PostMapping
    public ResponseEntity<Void> addItem(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestBody CartAddRequest request
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
            @RequestBody CartItemUpdateRequest request
    ) {
        cartService.updateCartItem(cartItemId, accessor, request);
        return ResponseEntity.ok().build();
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
            @RequestBody CartBulkDeleteRequest request
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

    @PostMapping("/checkout/reservations")
    public ResponseEntity<Void> checkoutToReservations(
            @Parameter(hidden = true) @Auth Accessor accessor
    ) {
        cartService.createReservationsFromSelected(accessor);
        return ResponseEntity.ok().build();
    }
}
