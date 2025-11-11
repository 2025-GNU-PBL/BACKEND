package gnu.project.backend.reservation.prefill.service;

import static gnu.project.backend.common.error.ErrorCode.RESERVATION_NOT_FOUND_EXCEPTION;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.reservation.dto.response.ReservationResponseDto;
import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.enumerated.Status;
import gnu.project.backend.reservation.prefill.dto.response.CreateDraftsResponse;
import gnu.project.backend.reservation.prefill.dto.response.ReservationPrefillResponse;
import gnu.project.backend.reservation.prefill.entity.ReservationPrefill;
import gnu.project.backend.reservation.prefill.repository.ReservationPrefillRepository;
import gnu.project.backend.reservation.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationPrefillService {

    private final ReservationPrefillRepository prefillRepository;
    private final ReservationRepository reservationRepository;

    public CreateDraftsResponse createFromCartItems(
            Customer customer,
            List<Product> products,
            List<Integer> quantities
    ) {
        LocalDateTime expires = LocalDateTime.now().plusMinutes(30);
        int size = products.size();

        List<Long> ids = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Product product = products.get(i);
            Integer quantity = quantities.get(i);

            ReservationPrefill saved = prefillRepository.save(
                    ReservationPrefill.create(
                            customer, product, quantity, expires
                    )
            );
            ids.add(saved.getId());
        }
        return new CreateDraftsResponse(ids);
    }

    @Transactional(readOnly = true)
    public ReservationPrefillResponse getPrefill(Long prefillId, Long customerId) {
        LocalDateTime now = LocalDateTime.now();
        ReservationPrefill p = prefillRepository
                .findActiveByIdAndCustomerId(prefillId, customerId, now)
                .orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND_EXCEPTION));

        Product prod = p.getProduct();
        return new ReservationPrefillResponse(
                p.getId(),
                prod.getId(),
                prod.getName(),
                prod.getPrice(),
                prod.getThumbnailUrl(),
                p.getQuantity()
        );
    }

    public ReservationResponseDto consumeToReservation(
            Long prefillId, Customer customer, String title, String content
    ) {
        LocalDateTime now = LocalDateTime.now();
        ReservationPrefill p = prefillRepository
                .findActiveByIdAndCustomerId(prefillId, customer.getId(), now)
                .orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND_EXCEPTION));


        Reservation reservation = Reservation.ofCreate(
                p.getProduct().getOwner(),
                customer,
                p.getProduct(),
                Status.PENDING,
                LocalDate.now(),
                title,
                content
        );
        reservationRepository.save(reservation);

        p.consume();
        return ReservationResponseDto.from(reservation);
    }
}
