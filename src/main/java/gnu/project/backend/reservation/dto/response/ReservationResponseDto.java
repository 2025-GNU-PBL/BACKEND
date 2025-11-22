package gnu.project.backend.reservation.dto.response;

import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.enumerated.Status;
import java.time.LocalDate;

public record ReservationResponseDto(
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
    String thumbnail
) {

    public static ReservationResponseDto from(
        final Reservation reservation
    ) {
        return new ReservationResponseDto(
            reservation.getId(),
            reservation.getOwner() != null ? reservation.getOwner().getId() : null,
            reservation.getCustomer() != null ? reservation.getCustomer().getId() : null,
            reservation.getProduct() != null ? reservation.getProduct().getId() : null,
            reservation.getStatus(),
            reservation.getReservationTime(),
            reservation.getOwner() != null ? reservation.getOwner().getBzName() : null,
            reservation.getProduct() != null ? reservation.getProduct().getName() : null,
            reservation.getProduct() != null ? reservation.getProduct().getPrice() : null,
            reservation.getCustomer() != null ? reservation.getCustomer().getName() : null,
            reservation.getCustomer() != null ? reservation.getCustomer().getPhoneNumber() : null,
            reservation.getCustomer() != null ? reservation.getCustomer().getEmail() : null,
            reservation.getTitle(),
            reservation.getContent(),
            reservation.getProduct() != null ? reservation.getProduct().getThumbnailUrl() : null
        );
    }
}
