package gnu.project.backend.payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.payment.dto.response.PaymentSettlementResponse;
import gnu.project.backend.payment.dto.response.QPaymentSettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static gnu.project.backend.order.entity.QOrder.order;
import static gnu.project.backend.payment.entity.QPayment.payment;
import static gnu.project.backend.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<PaymentSettlementResponse> findSettlementByOwnerId(Long ownerId) {
        return query
                .select(new QPaymentSettlementResponse(
                        product.name,
                        payment.amount.sum(),
                        payment.amount.sum(),
                        payment.id.count().intValue(),
                        order.orderCode,
                        payment.amount
                ))
                .from(payment)
                .join(payment.order, order)
                .join(order.orderDetails.any().product, product)
                .where(product.owner.id.eq(ownerId))
                .groupBy(order.orderCode, product.name)
                .fetch();
    }

    @Override
    public Optional<PaymentSettlementResponse> findOneForOwner(Long ownerId, String orderCode) {
        PaymentSettlementResponse result = query
                .select(new QPaymentSettlementResponse(
                        product.name,
                        payment.amount.sum(),
                        payment.amount.sum(),
                        payment.id.count().intValue(),
                        order.orderCode,
                        payment.amount
                ))
                .from(payment)
                .join(payment.order, order)
                .join(order.orderDetails.any().product, product)
                .where(
                        product.owner.id.eq(ownerId),
                        order.orderCode.eq(orderCode)
                )
                .groupBy(order.orderCode, product.name)
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
