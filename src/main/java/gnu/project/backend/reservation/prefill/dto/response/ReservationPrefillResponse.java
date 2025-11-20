package gnu.project.backend.reservation.prefill.dto.response;


public record ReservationPrefillResponse(
    Long prefillId,
    Long productId,
    String productName,
    String bzName,
    String ownerProfileImage,
    Integer price,
    String thumbnailUrl,
    Integer quantity
) {

}
