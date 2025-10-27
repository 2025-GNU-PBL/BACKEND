package gnu.project.backend.payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.payment.dto.response.PaymentSettlementResponse;
import gnu.project.backend.payment.dto.response.QPaymentSettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gnu.project.backend.order.entity.QOrder.order;
import static gnu.project.backend.payment.entity.QPayment.payment;
import static gnu.project.backend.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryDslImpl implements PaymentRepositoryDsl {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<PaymentSettlementResponse> findSettlementByOwner(Long ownerId) {
        return queryFactory
                .select(new QPaymentSettlementResponse(
                        product.name,
                        payment.amount.sum(),
                        payment.amount.sum(), // 환불 반영 로직 추후 변경
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

}
