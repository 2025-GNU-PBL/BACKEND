package gnu.project.backend.payment.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.payment.dto.response.*;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static gnu.project.backend.common.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;
    private final OwnerRepository ownerRepository;

    public List<PaymentListResponse> getMyPayments(String socialId, int page, int size) {
        List<Payment> payments =
                paymentRepository.findAllWithOrderAndDetailsByCustomerSocialId(socialId, page, size);

        return payments.stream()
                .map(p -> {
                    Order order = p.getOrder();
                    return new PaymentListResponse(
                            order.getOrderCode(),
                            order.getShopName(),
                            order.getMainProductName(),
                            order.getThumbnailUrl(),
                            p.getPaymentKey(),
                            p.getAmount(),
                            p.getStatus(),
                            p.getApprovedAt()
                    );
                })
                .toList();
    }

    public PaymentDetailResponse getDetail(String paymentKey, Accessor accessor) {
        Payment p = paymentRepository.findWithOrderAndDetailsByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        Order order = p.getOrder();

        String customerSocialId = order.getCustomerSocialId();
        boolean isCustomer = customerSocialId != null && customerSocialId.equals(accessor.getSocialId());

        String ownerSocialId = order.getMainProductOwnerSocialId();
        boolean isOwner = ownerSocialId != null && ownerSocialId.equals(accessor.getSocialId());

        if (!isCustomer && !isOwner) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        return new PaymentDetailResponse(
                order.getOrderCode(),

                // 상품 정보
                order.getShopName(),
                order.getMainProductName(),
                order.getThumbnailUrl(),

                // 결제 내역
                p.getPaymentKey(),
                order.getOriginalPrice(),
                order.getDiscountAmount(),
                order.getTotalPrice(),
                p.getAmount(),

                // 상태/메타
                p.getStatus(),
                p.getApprovedAt(),
                p.getCanceledAt(),
                p.getCancelReason(),
                p.getReceiptUrl(),
                p.getPaymentMethod(),
                p.getPgProvider()
        );
    }

    public PaymentSettlementResponse getMySettlement(
            Accessor accessor,
            Integer year,
            Integer month,
            PaymentStatus status,
            int page,
            int size
    ) {
        Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));

        List<Payment> payments = paymentRepository.findAllWithOrderAndDetailsByOwnerId(owner.getId());

        // 필터링
        Stream<Payment> stream = payments.stream();

        if (year != null && month != null) {
            stream = stream.filter(p -> {
                LocalDateTime approvedAt = p.getApprovedAt();
                return approvedAt != null
                        && approvedAt.getYear() == year
                        && approvedAt.getMonthValue() == month;
            });
        }

        if (status != null) {
            stream = stream.filter(p -> p.getStatus() == status);
        }

        List<Payment> filtered = stream.toList();

        long totalSales = 0L;
        long expectedSettlement = 0L;
        int completedCount = 0;
        int cancelCount = 0;

        for (Payment p : filtered) {
            switch (p.getStatus()) {
                case DONE -> {
                    totalSales += p.getAmount();
                    expectedSettlement += p.getAmount();
                    completedCount++;
                }
                case CANCELED -> {
                    totalSales -= p.getAmount();
                    expectedSettlement -= p.getAmount();
                    cancelCount++;
                }
                default -> {
                    // 나머지 상태는 정산/취소 집계에서 제외
                }
            }
        }

        PaymentSettlementSummaryResponse summary = new PaymentSettlementSummaryResponse(
                owner.getBzName(),
                Math.max(0L, totalSales),
                Math.max(0L, expectedSettlement),
                completedCount,
                cancelCount
        );

        List<PaymentSettlementSummaryItemResponse> allItems = filtered.stream()
                .map(p -> new PaymentSettlementSummaryItemResponse(
                        p.getPaymentKey(),
                        p.getOrder().getOrderCode(),
                        p.getOrder().getCustomer().getName(),
                        p.getAmount(),
                        p.getStatus(),
                        p.getApprovedAt()
                ))
                .toList();

        List<PaymentSettlementSummaryItemResponse> pagedItems = applyPaging(allItems, page, size);

        return new PaymentSettlementResponse(summary, pagedItems);
    }

    public List<PaymentCancelResponse> getMyCancelRequests(Accessor accessor) {
        Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));

        return paymentRepository.findAllCancelRequestedWithOrderAndDetailsByOwnerId(owner.getId())
                .stream()
                .map(PaymentCancelResponse::from)
                .toList();
    }

    private <T> List<T> applyPaging(List<T> list, int page, int size) {
        if (size <= 0) return list;

        int safePage = Math.max(page, 0);
        int fromIndex = safePage * size;
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + size, list.size());
        return list.subList(fromIndex, toIndex);
    }

}
