package gnu.project.backend.order.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.order.controller.docs.OrderDocs;
import gnu.project.backend.order.dto.request.OrderCreateRequest;
import gnu.project.backend.order.dto.response.OrderResponse;
import gnu.project.backend.order.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderDocs {

    private final OrderService orderService;

    @Override
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @Parameter(hidden = true) @Auth Accessor accessor
    ) {
        return ResponseEntity.ok(orderService.findMyOrders(accessor));
    }

    @Override
    @GetMapping("/{orderCode}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderCode) {
        return ResponseEntity.ok(orderService.findByOrderCode(orderCode));
    }

    @Override
    @PostMapping("/from-reservation")
    public ResponseEntity<OrderResponse> createFromReservation(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestBody OrderCreateRequest request
    ) {
        return ResponseEntity.ok(orderService.createFromReservation(accessor, request));
    }
}
