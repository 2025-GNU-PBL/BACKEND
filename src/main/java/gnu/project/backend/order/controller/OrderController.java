package gnu.project.backend.order.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.order.dto.response.OrderResponse;
import gnu.project.backend.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> findMyOrders(@Auth final Accessor accessor) {
        List<OrderResponse> myOrders = orderService.findMyOrders(accessor);
        return ResponseEntity.ok(myOrders);
    }
}
