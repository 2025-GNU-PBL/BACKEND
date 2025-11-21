package gnu.project.backend.payment.service;

import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.PAYMENT_ACCESS_DENIED;
import static gnu.project.backend.common.error.ErrorCode.PAYMENT_NOT_FOUND;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.payment.dto.response.PaymentCancelResponse;
import gnu.project.backend.payment.dto.response.PaymentDetailResponse;
import gnu.project.backend.payment.dto.response.PaymentListResponse;
import gnu.project.backend.payment.dto.response.PaymentSettlementResponse;
import gnu.project.backend.payment.dto.response.PaymentSettlementSummaryItemResponse;
import gnu.project.backend.payment.dto.response.PaymentSettlementSummaryResponse;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                        p.getPaymentKey(),
                        order.getOrderCode(),
                        order.getShopName(),
                        order.getMainProductName(),
                        order.getThumbnailUrl(),
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
        boolean isCustomer =
            customerSocialId != null && customerSocialId.equals(accessor.getSocialId());

        String ownerSocialId = order.getMainProductOwnerSocialId();
        boolean isOwner = ownerSocialId != null && ownerSocialId.equals(accessor.getSocialId());

        if (!isCustomer && !isOwner) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        return buildPaymentDetailResponse(p, order);
    }

    public PaymentDetailResponse getCanceledDetail(String paymentKey, Accessor accessor) {
        Payment p = paymentRepository.findWithOrderAndDetailsByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        Order order = p.getOrder();

        String ownerSocialId = order.getMainProductOwnerSocialId();
        boolean isOwner = ownerSocialId != null && ownerSocialId.equals(accessor.getSocialId());

        if (!accessor.isOwner() || !isOwner) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        if (p.getStatus() != PaymentStatus.CANCELED) {
            throw new BusinessException(PAYMENT_NOT_FOUND);
        }

        return  buildPaymentDetailResponse(p, order);
    }

    public PaymentDetailResponse getCancelRequestDetail(String paymentKey, Accessor accessor) {
        Payment p = paymentRepository.findWithOrderAndDetailsByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        Order order = p.getOrder();

        String ownerSocialId = order.getMainProductOwnerSocialId();
        boolean isOwner = ownerSocialId != null && ownerSocialId.equals(accessor.getSocialId());

        if (!accessor.isOwner() || !isOwner) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        if (p.getStatus() != PaymentStatus.CANCEL_REQUESTED) {
            throw new BusinessException(PAYMENT_NOT_FOUND);
        }

        return buildPaymentDetailResponse(p, order);
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

        List<Payment> payments = paymentRepository.findAllWithOrderAndDetailsByOwnerId(
            owner.getId());

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

    public List<PaymentCancelResponse> getMyCanceledPayments(Accessor accessor) {
        Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));

        return paymentRepository.findAllCanceledWithOrderAndDetailsByOwnerId(owner.getId())
                .stream()
                .map(PaymentCancelResponse::from)
                .toList();
    }

    private PaymentDetailResponse buildPaymentDetailResponse(Payment p, Order order) {
        return new PaymentDetailResponse(
                p.getPaymentKey(),
                order.getOrderCode(),

                order.getShopName(),
                order.getMainProductName(),
                order.getThumbnailUrl(),

                order.getCustomer().getEmail(),
                order.getCustomer().getName(),
                order.getCustomer().getPhoneNumber(),

                order.getOriginalPrice(),
                order.getDiscountAmount(),
                order.getTotalPrice(),
                p.getAmount(),

                p.getStatus(),
                p.getApprovedAt(),
                p.getCanceledAt(),
                p.getCancelReason(),
                p.getReceiptUrl(),
                p.getPaymentMethod(),
                p.getPgProvider(),
                p.getCancelRejectReason(),
                p.getCancelRejectAt()
        );
    }

    private <T> List<T> applyPaging(List<T> list, int page, int size) {
        if (size <= 0) {
            return list;
        }

        int safePage = Math.max(page, 0);
        int fromIndex = safePage * size;
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + size, list.size());
        return list.subList(fromIndex, toIndex);
    }

}
