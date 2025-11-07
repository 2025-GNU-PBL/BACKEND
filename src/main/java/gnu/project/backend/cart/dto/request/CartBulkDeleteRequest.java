package gnu.project.backend.cart.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CartBulkDeleteRequest(
       @NotEmpty List<Long> cartItemIds
) {
}
