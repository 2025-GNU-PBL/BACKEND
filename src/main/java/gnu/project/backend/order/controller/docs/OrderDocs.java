package gnu.project.backend.order.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.order.dto.request.CouponPreviewRequest;
import gnu.project.backend.order.dto.request.OrderCreateRequest;
import gnu.project.backend.order.dto.response.CouponPreviewResponse;
import gnu.project.backend.order.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Order", description = "주문 API")
public interface OrderDocs {

    @Operation(summary = "내 주문 목록", description = "로그인한 사용자의 주문들을 조회합니다.")
    ResponseEntity<List<OrderResponse>> getMyOrders(
            @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(summary = "주문 상세", description = "orderCode로 주문 하나를 조회합니다.")
    ResponseEntity<OrderResponse> getOrder(
            @PathVariable String orderCode
    );

    @Operation(summary = "예약으로부터 주문 생성",
            description = "예약(PENDING/APPROVE) 기반으로 주문을 생성하고, 필요하면 쿠폰도 함께 적용합니다.")
    ResponseEntity<OrderResponse> createFromReservation(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestBody OrderCreateRequest request
    );

    @Operation(summary = "쿠폰 미리보기", description = "현재 주문에 특정 고객쿠폰 적용 시 할인액/총액 미리보기.")
    ResponseEntity<CouponPreviewResponse> previewCoupon(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable String orderCode,
            @RequestBody CouponPreviewRequest request
    );

    @Operation(summary = "쿠폰 적용", description = "현재 주문에 특정 고객쿠폰을 적용하거나 해제합니다(파라미터 null 시 해제).")
    ResponseEntity<OrderResponse> applyCoupon(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable String orderCode,
            Long userCouponId
    );
}
