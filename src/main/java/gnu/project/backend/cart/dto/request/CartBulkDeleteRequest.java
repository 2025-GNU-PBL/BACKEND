package gnu.project.backend.cart.dto.request;

import java.util.List;

public record CartBulkDeleteRequest(
        List<Long> cartItemIds
) {
}
