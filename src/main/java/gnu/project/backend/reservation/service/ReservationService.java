package gnu.project.backend.reservation.service;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_SOCIAL;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
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
import gnu.project.backend.reservation.dto.request.ReservationRequestDto;
import gnu.project.backend.reservation.dto.request.ReservationStatusChangeRequestDto;
import gnu.project.backend.reservation.dto.response.ReservationResponseDto;
import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.enumerated.Status;
import gnu.project.backend.reservation.event.ReservationApprovedEvent;
import gnu.project.backend.reservation.repository.ReservationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final OwnerRepository ownerRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

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
        final Reservation reservation = reservationRepository.findReservationByIdWithOwner(
                requestDto.id())
            .orElseThrow(() -> new BusinessException(STUDIO_NOT_FOUND_EXCEPTION));
        //TODO : 사장만 해당 상태를 변경할 수 있나? 고객의 단순 변심으로는 변경이 안되나?
        if (!reservation.getOwner().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }
        reservation.changeStatus(requestDto.status());
        if (reservation.getStatus() == Status.APPROVE) {
            eventPublisher.publishEvent(
                new ReservationApprovedEvent(
                    reservation.getId(),
                    reservation.getReservationTime(),
                    reservation.getTitle(),
                    reservation.getContent()
                )
            );
            log.info("예약 승인 이벤트 발행 완료 - Reservation ID: {}", reservation.getId());
        }

        return ReservationResponseDto.from(reservation);
    }

    public List<ReservationResponseDto> findReservations(final Accessor accessor) {
        switch (accessor.getUserRole()) {
            case CUSTOMER -> {
                return findCustomerReservations(accessor);
            }
            case OWNER -> {
                return findOwnerReservations(accessor);
            }
        }
        return List.of();
    }

    private List<ReservationResponseDto> findCustomerReservations(final Accessor accessor) {
        final Customer customer = customerRepository.
            findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));

        List<Reservation> reservations = reservationRepository
            .findReservationsByCustomerId(customer.getId());

        return reservations.stream()
            .map(ReservationResponseDto::from)
            .toList();
    }

    private List<ReservationResponseDto> findOwnerReservations(final Accessor accessor) {
        final Owner owner = ownerRepository.
            findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));

        List<Reservation> reservations = reservationRepository
            .findReservationsByOwnerId(owner.getId());

        return reservations.stream()
            .map(ReservationResponseDto::from)
            .toList();
    }
}
