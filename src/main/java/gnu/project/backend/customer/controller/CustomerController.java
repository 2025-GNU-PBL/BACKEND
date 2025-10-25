package gnu.project.backend.customer.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;

import gnu.project.backend.customer.controller.docs.CustomerDocs;
import gnu.project.backend.customer.dto.request.CustomerRequest;
import gnu.project.backend.customer.dto.response.CustomerResponse;
import gnu.project.backend.customer.dto.response.CustomerSignInResponse;
import gnu.project.backend.customer.dto.response.CustomerUpdateResponse;
import gnu.project.backend.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController implements CustomerDocs {

    private final CustomerService customerService;

    @Override
    @PostMapping
    public ResponseEntity<CustomerSignInResponse> signUp(
            @Auth final Accessor accessor,
            @RequestBody @Valid final CustomerRequest request
    ) {
        return ResponseEntity.ok(customerService.signUp(accessor, request));
    }

    @Override
    @PatchMapping
    public ResponseEntity<CustomerUpdateResponse> update(
            @Auth final Accessor accessor,
            @RequestBody @Valid final CustomerRequest request
    ) {
        return ResponseEntity.ok(customerService.update(accessor, request));
    }

    @Override
    @GetMapping
    public ResponseEntity<CustomerResponse> find(
            @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(customerService.findCustomer(accessor));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> withdraw(
            @Auth final Accessor accessor
    ) {
        customerService.withdraw(accessor);
        return ResponseEntity.noContent().build();
    }

}