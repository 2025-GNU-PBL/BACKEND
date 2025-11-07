package gnu.project.backend.cart.dto.request;

import jakarta.validation.constraints.Min;

public record CartItemUpdateRequest(
        @Min(1)Integer quantity,
        Boolean selected
) {
}
