package gnu.project.backend.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public record CartAddRequest(
        @NotNull Long productId,
        @NotNull
        @Min(1)Integer quantity
) {
}
