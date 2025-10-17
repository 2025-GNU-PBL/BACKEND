package gnu.project.backend.reservaiton.service;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_SOCIAL;
import static gnu.project.backend.common.error.ErrorCode.PRODUCT_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.STUDIO_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ProductRepository;
import gnu.project.backend.reservaiton.dto.request.ReservationRequestDto;
import gnu.project.backend.reservaiton.dto.request.ReservationStatusChangeRequestDto;
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
        final Customer customer = customerRepository
            .findByOauthInfo_SocialId(accessor.getSocialId())
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

    public ReservationResponseDto changeStatus(
        final Accessor accessor,
        final ReservationStatusChangeRequestDto requestDto
    ) {
        final Reservation reservation = reservationRepository.findByIdWithOwner(requestDto.id())
            .orElseThrow(() -> new BusinessException(STUDIO_NOT_FOUND_EXCEPTION));
        //TODO : 사장만 해당 상태를 변경할 수 있나? 고객의 단순 변심으로는 변경이 안되나?
        if (reservation.getOwner().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }
        //TODO : APPROVE 일시 이벤트 기반으로 Schedule을 생성해줘야함
//        if (requestDto.status() == Status.APPROVE)
        reservation.changeStatus(requestDto.status());

        return ReservationResponseDto.from(reservation);
    }
}
