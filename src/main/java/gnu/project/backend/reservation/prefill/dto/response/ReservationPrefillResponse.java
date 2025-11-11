package gnu.project.backend.reservation.prefill.dto.response;


public record ReservationPrefillResponse(
        Long prefillId,
        Long productId,
        String productName,
        Integer price,
        String thumbnailUrl,
        Integer quantity
) {}
