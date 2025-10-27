package gnu.project.backend.customer.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;

import gnu.project.backend.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping()
    public ResponseEntity<?> read(@Auth final Accessor accessor) {
        return ResponseEntity.ok(customerService.read(accessor));
    }

    @GetMapping("/me/test-auth")
    public ResponseEntity<String> testAuthentication(@Auth Accessor accessor) {
        String message = "인증 성공! 현재 로그인된 사용자의 socialId는 " + accessor.getSocialId() + " 입니다.";
        return ResponseEntity.ok(message);
    }

}