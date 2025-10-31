package gnu.project.backend.cart.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CartAddRequest(
        @NotNull Long productId,
        Long optionId,
        LocalDateTime desireDate,
        Integer quantity,
        String memo
) {
}
