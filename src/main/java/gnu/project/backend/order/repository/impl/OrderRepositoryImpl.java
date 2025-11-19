package gnu.project.backend.order.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.customer.entity.QCustomer;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.entity.QOrder;
import gnu.project.backend.order.entity.QOrderDetail;
import gnu.project.backend.order.repository.OrderCustomRepository;
import gnu.project.backend.product.entity.QProduct;
import gnu.project.backend.reservation.entity.QReservation;
import gnu.project.backend.owner.entity.QOwner;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory queryFactory;

    private static final QProduct product = QProduct.product;
    private static final QOrder order = QOrder.order;
    private static final QOrderDetail orderDetail = QOrderDetail.orderDetail;
    private static final QCustomer customer = QCustomer.customer;
    private static final QReservation reservation = QReservation.reservation;
    private static final QProduct reservationProduct = QProduct.product;
    private static final QOwner owner = QOwner.owner;
    private static final QProduct detailProduct = new QProduct("detailProduct");

    @Override
    public List<Order> findAllByCustomerSocialId(String socialId) {
        return queryFactory
                .select(order)
                .distinct()
                .from(order)
                .leftJoin(order.customer, customer).fetchJoin()
                .leftJoin(order.reservation, reservation).fetchJoin()
                .leftJoin(reservation.product, reservationProduct).fetchJoin()
                .leftJoin(reservationProduct.owner, owner).fetchJoin()
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, detailProduct).fetchJoin()
                .where(
                        customer.oauthInfo.socialId.eq(socialId),
                        order.isDeleted.isFalse()
                )
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<Order> findByOrderCodeWithDetails(String orderCodeStr) {
        Order result = queryFactory
                .select(order)
                .distinct()
                .from(order)
                .leftJoin(order.customer, customer).fetchJoin()
                .leftJoin(order.reservation, reservation).fetchJoin()
                .leftJoin(reservation.product, reservationProduct).fetchJoin()
                .leftJoin(reservationProduct.owner, owner).fetchJoin()
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, detailProduct).fetchJoin()
                .where(
                        order.orderCode.eq(orderCodeStr),
                        order.isDeleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsPaidByCustomerAndProduct(Long customerId, Long productId) {
        Integer hit = queryFactory
                .selectOne()
                .from(order)
                .join(order.customer, customer)
                .join(order.orderDetails, orderDetail)
                .join(orderDetail.product, product)
                .where(
                        customer.id.eq(customerId),
                        product.id.eq(productId),
                        order.status.eq(OrderStatus.PAID)
                )
                .fetchFirst();
        return hit != null;
    }
}
