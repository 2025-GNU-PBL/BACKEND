package gnu.project.backend.reservaiton.service;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.PRODUCT_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ProductRepository;
import gnu.project.backend.reservaiton.dto.request.ReservationRequestDto;
import gnu.project.backend.reservaiton.dto.response.ReservationResponseDto;
import gnu.project.backend.reservaiton.entity.Reservation;
import gnu.project.backend.reservaiton.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final OwnerRepository ownerRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public ReservationResponseDto createReservation(
        final Accessor accessor,
        final ReservationRequestDto requestDto
    ) {
        final Customer customer = customerRepository.
            findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(
                CUSTOMER_NOT_FOUND_EXCEPTION)
            );
        final Product product = productRepository.findById(requestDto.productId()).orElseThrow(
            () -> new BusinessException(PRODUCT_NOT_FOUND_EXCEPTION)
        );
        final Owner owner = product.getOwner();
        final Reservation reservation = Reservation.ofCreate(
            owner,
            customer,
            product,
            requestDto.status(),
            requestDto.reservationTime(),
            requestDto.title(),
            requestDto.content()
        );
        final Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponseDto.from(savedReservation);
    }
    
}
