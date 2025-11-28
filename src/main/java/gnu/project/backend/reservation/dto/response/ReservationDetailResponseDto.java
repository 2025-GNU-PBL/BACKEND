package gnu.project.backend.reservation.dto.response;

import gnu.project.backend.reservation.enumerated.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDetailResponseDto(
    Long id,
    Long ownerId,
    Long customerId,
    Long productId,
    Status status,
    LocalDate reservationTime,
    String storeName,
    String productName,
    Integer price,
    String customerName,
    String customerPhoneNumber,
    String customerEmail,
    String title,
    String content,
    String thumbnail,
    LocalDateTime createdAt
) {


}
