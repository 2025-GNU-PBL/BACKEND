package gnu.project.backend.customer.controller;

import gnu.project.backend.customer.dto.CustomerCreateRequest;
import gnu.project.backend.customer.dto.CustomerResponse;
import gnu.project.backend.customer.dto.CustomerUpdateRequest;
import gnu.project.backend.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    //Create (생성)
    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody CustomerCreateRequest request) {
        String customerId = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerId);
    }

    //READ (읽기)
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String customerId) {
        CustomerResponse response = customerService.getCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    // UPDATE (수정)
    @PutMapping("/{customerId}")
    public ResponseEntity<Void> updateCustomer(@PathVariable String customerId, @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok().build();
    }

    //DELETE (삭제)
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}