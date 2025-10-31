package gnu.project.backend.payment.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.payment.dto.response.PaymentDetailResponse;
import gnu.project.backend.payment.dto.response.PaymentListResponse;
import gnu.project.backend.payment.dto.response.PaymentSettlementResponse;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static gnu.project.backend.common.error.ErrorCode.PAYMENT_ACCESS_DENIED;
import static gnu.project.backend.common.error.ErrorCode.PAYMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public List<PaymentListResponse> getMyPayments(String socialId) {
        return paymentRepository.findByOrder_Customer_OauthInfo_SocialId(socialId)
                .stream()
                .map(p -> new PaymentListResponse(
                        p.getOrder().getOrderCode(),
                        p.getOrder().getOrderDetails().isEmpty()
                                ? "상품 없음"
                                : p.getOrder().getOrderDetails().get(0).getProduct().getName(),
                        p.getAmount(),
                        p.getStatus(),
                        p.getApprovedAt()
                ))
                .toList();
    }

    public PaymentDetailResponse getDetail(String paymentKey, Accessor accessor) {
        Payment p = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        boolean isCustomer = p.getOrder().getCustomer().getOauthInfo().getSocialId()
                .equals(accessor.getSocialId());

        String ownerSocialId = p.getOrder().getOrderDetails().get(0).getProduct()
                .getOwner().getOauthInfo().getSocialId();
        boolean isOwner = ownerSocialId.equals(accessor.getSocialId());

        if (!isCustomer && !isOwner) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        String productName = p.getOrder().getOrderDetails().isEmpty()
                ? "상품 없음"
                : p.getOrder().getOrderDetails().get(0).getProduct().getName();

        return new PaymentDetailResponse(
                p.getOrder().getOrderCode(),
                productName,
                p.getAmount(),
                p.getStatus(),
                p.getApprovedAt(),
                p.getCanceledAt(),
                p.getCancelReason(),
                p.getReceiptUrl(),
                p.getPaymentMethod(),
                p.getPgProvider()
        );
    }

    public List<PaymentSettlementResponse> getMySettlement(Long ownerId) {
        return paymentRepository.findSettlementByOwnerId(ownerId);
    }
}
