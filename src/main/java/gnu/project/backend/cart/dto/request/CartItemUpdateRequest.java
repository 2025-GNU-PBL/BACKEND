package gnu.project.backend.cart.dto.request;

public record CartItemUpdateRequest(
        Integer quantity,
        Boolean selected
) {
}
