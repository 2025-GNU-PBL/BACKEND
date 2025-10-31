package gnu.project.backend.order.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.entity.QOrder;
import gnu.project.backend.order.entity.QOrderDetail;
import gnu.project.backend.order.repository.OrderCustomRepository;
import gnu.project.backend.reservation.entity.QReservation;
import gnu.project.backend.customer.entity.QCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory queryFactory;

    private static final QOrder order = QOrder.order;
    private static final QOrderDetail orderDetail = QOrderDetail.orderDetail;
    private static final QCustomer customer = QCustomer.customer;
    private static final QReservation reservation = QReservation.reservation;

    @Override
    public List<Order> findAllByCustomerSocialId(String socialId) {
        return queryFactory
                .selectFrom(order)
                .leftJoin(order.customer, customer).fetchJoin()
                .leftJoin(order.reservation, reservation).fetchJoin()
                .where(customer.oauthInfo.socialId.eq(socialId),
                        order.isDeleted.isFalse())
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<Order> findByOrderCodeWithDetails(String orderCodeStr) {
        Order result = queryFactory
                .selectFrom(order)
                .leftJoin(order.customer, customer).fetchJoin()
                .leftJoin(order.reservation, reservation).fetchJoin()
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .where(order.orderCode.eq(orderCodeStr),
                        order.isDeleted.isFalse())
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
