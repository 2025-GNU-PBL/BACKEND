package gnu.project.backend.reservation.repository;

import gnu.project.backend.reservation.dto.response.ReservationDetailResponseDto;
import gnu.project.backend.reservation.entity.Reservation;
import java.util.List;
import java.util.Optional;

public interface ReservationCustomRepository {

    Optional<Reservation> findByIdWithAllRelations(final Long id);

    Optional<Reservation> findReservationByIdWithOwner(final Long id);

    List<Reservation> findReservationsByOwnerId(final Long id);

    List<Reservation> findReservationsByCustomerId(final Long id);

    Optional<ReservationDetailResponseDto> findReservationDetailById(final Long id);
}
