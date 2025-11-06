package gnu.project.backend.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CartAddRequest(
        @NotNull Long productId,
        LocalDateTime desireDate,
        @Min(1)Integer quantity
        // @Size(max = 500) String memo
) {
}
