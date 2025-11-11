package gnu.project.backend.payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static gnu.project.backend.customer.entity.QCustomer.customer;
import static gnu.project.backend.order.entity.QOrder.order;
import static gnu.project.backend.order.entity.QOrderDetail.orderDetail;
import static gnu.project.backend.owner.entity.QOwner.owner;
import static gnu.project.backend.payment.entity.QPayment.payment;
import static gnu.project.backend.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Payment> findAllWithOrderAndDetailsByCustomerSocialId(String socialId) {
        return query
                .selectFrom(payment)
                .distinct()
                .join(payment.order, order).fetchJoin()
                .join(order.customer, customer).fetchJoin()
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, product).fetchJoin()
                .leftJoin(product.owner, owner).fetchJoin()
                .where(order.customer.oauthInfo.socialId.eq(socialId))
                .orderBy(payment.approvedAt.desc())
                .fetch();
    }

    @Override
    public Optional<Payment> findWithOrderAndDetailsByPaymentKey(String paymentKeyStr) {
        Payment result = query
                .selectFrom(payment)
                .distinct()
                .join(payment.order, order).fetchJoin()
                .join(order.customer, customer).fetchJoin()
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, product).fetchJoin()
                .leftJoin(product.owner, owner).fetchJoin()
                .where(payment.paymentKey.eq(paymentKeyStr))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Payment> findAllWithOrderAndDetailsByOwnerId(Long ownerId) {
        return query
                .selectFrom(payment)
                .distinct()
                .join(payment.order, order).fetchJoin()
                .join(order.customer, customer).fetchJoin()
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, product).fetchJoin()
                .join(product.owner, owner).fetchJoin()
                .where(owner.id.eq(ownerId))
                .orderBy(payment.approvedAt.desc())
                .fetch();
    }

    @Override
    public List<Payment> findAllCancelRequestedWithOrderAndDetailsByOwnerId(Long ownerId) {
        return query
                .selectFrom(payment)
                .distinct()
                .join(payment.order, order).fetchJoin()
                .join(order.customer, customer).fetchJoin()
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, product).fetchJoin()
                .join(product.owner, owner).fetchJoin()
                .where(
                        owner.id.eq(ownerId),
                        payment.status.eq(PaymentStatus.CANCEL_REQUESTED) 
                )
                .orderBy(payment.approvedAt.desc())
                .fetch();
    }
}
