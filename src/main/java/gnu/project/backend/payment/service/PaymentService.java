package gnu.project.backend.payment.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.payment.dto.response.PaymentDetailResponse;
import gnu.project.backend.payment.dto.response.PaymentListResponse;
import gnu.project.backend.payment.dto.response.PaymentSettlementResponse;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.repository.OrderRepository;
import gnu.project.backend.payment.repository.PaymentRepositoryDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static gnu.project.backend.common.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentRepositoryDsl paymentRepositoryDsl;

    // TODO:   TYPE MAPPING 변경 필요
    public List<PaymentListResponse> getPaymentHistory(String socialId) {
        return paymentRepository.findByOrder_Customer_OauthInfo_SocialId(socialId)
                .stream()
                .map(p -> {
                    String productName = p.getOrder().getOrderDetails().isEmpty()
                            ? "상품 없음"
                            : p.getOrder().getOrderDetails().get(0).getProduct().getName();

                    return new PaymentListResponse(
                            p.getOrder().getOrderCode(),
                            productName,
                            p.getAmount(),
                            p.getStatus(),
                            p.getApprovedAt()
                    );
                })
                .toList();
    }
// TODO : 추후 확장
    public PaymentDetailResponse getPaymentDetail(String paymentKey, Accessor accessor) {
        Payment p = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        boolean isCustomer = p.getOrder().getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId());
        String ownerSocialId = p.getOrder().getOrderDetails().get(0).getProduct().getOwner().getOauthInfo().getSocialId();
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

//   TODO : 추후 변경
    public List<PaymentSettlementResponse> getSettlementByOwner(Long ownerId) {

//        if (!accessor.isOwner()) {
//            throw new BusinessException(AUTH_FORBIDDEN); // 사장님만 이 기능 사용 가능
//        }
//
//        Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
//                .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));

        return paymentRepositoryDsl.findSettlementByOwner(ownerId);
    }



}
