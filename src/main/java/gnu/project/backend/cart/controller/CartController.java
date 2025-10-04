package gnu.project.backend.cart.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.cart.dto.request.AddCartItemRequest;
import gnu.project.backend.cart.dto.response.CartItemResponse;
import gnu.project.backend.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @Auth final Accessor accessor,
            @RequestBody final AddCartItemRequest request
    ) {
        cartService.addCart(accessor, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/myCart")
    public ResponseEntity<List<CartItemResponse>> getMyCart(@Auth final Accessor accessor) {
        List<CartItemResponse> myCart = cartService.getMyCart(accessor);
        return ResponseEntity.ok(myCart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable final Long cartItemId,
            @Auth final Accessor accessor
    ) throws Throwable {
        cartService.deleteCartItem(cartItemId, accessor);
        return ResponseEntity.noContent().build();
    }
}
