package gnu.project.backend.reservation.repository.impl;

import static gnu.project.backend.customer.entity.QCustomer.customer;
import static gnu.project.backend.owner.entity.QOwner.owner;
import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QProduct.product;
import static gnu.project.backend.reservation.entity.QReservation.reservation;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.reservation.dto.response.ReservationDetailResponseDto;
import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.repository.ReservationCustomRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationCustomRepository {

    private static final OrderSpecifier<?>[] RESERVATION_DEFAULT_ORDER = {
        reservation.createdAt.desc(),
        reservation.id.desc()
    };
    private final JPAQueryFactory query;

    @Override
    public Optional<Reservation> findByIdWithAllRelations(final Long id) {
        return Optional.ofNullable(
            query
                .selectFrom(reservation)
                .leftJoin(reservation.owner, owner).fetchJoin()
                .leftJoin(reservation.customer, customer).fetchJoin()
                .leftJoin(reservation.product, product).fetchJoin()
                .where(reservation.id.eq(id))
                .fetchFirst()
        );
    }

    @Override
    public Optional<Reservation> findReservationByIdWithOwner(final Long id) {
        return Optional.ofNullable(
            query
                .selectFrom(reservation)
                .leftJoin(reservation.owner, owner)
                .fetchJoin()
                .where(reservation.id.eq(id))
                .fetchFirst()
        );
    }


    @Override
    public List<Reservation> findReservationsByOwnerId(final Long id) {
        return query
            .selectFrom(reservation)
            .leftJoin(reservation.owner, owner)
            .fetchJoin()
            .where(reservation.owner.id.eq(id))
            .orderBy(RESERVATION_DEFAULT_ORDER)
            .fetch();
    }

    @Override
    public List<Reservation> findReservationsByCustomerId(final Long id) {
        return query
            .selectFrom(reservation)
            .leftJoin(reservation.customer, customer)
            .fetchJoin()
            .where(reservation.customer.id.eq(id))
            .orderBy(RESERVATION_DEFAULT_ORDER)
            .fetch();
    }

    @Override
    public Optional<ReservationDetailResponseDto> findReservationDetailById(final Long id) {
        BooleanBuilder imageCondition = new BooleanBuilder();
        imageCondition.and(image.displayOrder.eq(0));
        return Optional.ofNullable(
            query.select(createReservationDetailResponse())
                .from(reservation)
                .leftJoin(reservation.owner, owner)
                .leftJoin(reservation.customer, customer)
                .leftJoin(reservation.product, product)
                .leftJoin(product.images, image)
                .where(reservation.id.eq(id), imageCondition)
                .fetchOne()
        );
    }

    private ConstructorExpression<ReservationDetailResponseDto> createReservationDetailResponse() {
        return Projections.constructor(
            ReservationDetailResponseDto.class,
            reservation.id,
            owner.id,
            customer.id,
            product.id,
            reservation.status,
            reservation.reservationTime,
            owner.bzName,
            product.name,
            product.price,
            customer.oauthInfo.name,
            customer.phoneNumber,
            customer.oauthInfo.email,
            reservation.title,
            reservation.content,
            image.url
        );
    }
}
