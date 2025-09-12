package gnu.project.backend.customer.service;

import gnu.project.backend.customer.dto.CustomerCreateRequest;
import gnu.project.backend.customer.dto.CustomerResponse;
import gnu.project.backend.customer.dto.CustomerUpdateRequest;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
// CREATE
    public String createCustomer(CustomerCreateRequest request){

        Customer customer = new Customer(
                request.getId(),
                request.getSocialId2(),
                request.getRole(),
                request.getProfilePicture(),
                request.getAge(),
                request.getPhoneNumber(),
                request.getAddress(),
                request.getBankAccount()
        );
        Customer savedCustomer = customerRepository.save(customer);
        return savedCustomer.getId();
    }

    //  Read
    @Transactional(readOnly = true) // 데이터를 변경하지 않는 조회 메서드는 readOnly=true 옵션으로 성능을 최적화합니다.
    public CustomerResponse getCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 ID입니다: " + customerId));
        return new CustomerResponse(customer);
    }

    //UPDATE
    public void updateCustomer(String customerId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 ID입니다: " + customerId));

        // Customer Entity에 정의된 update 메서드를 호출하여 객체의 상태를 변경합니다.
        customer.update(
                request.getProfilePicture(),
                request.getPhoneNumber(),
                request.getAddress(),
                request.getBankAccount()
        );
        // @Transactional에 의해 메서드가 종료될 때 변경된 내용이 자동으로 DB에 반영(UPDATE 쿼리 실행)됩니다.
    }

    //DELETE
    public void deleteCustomer(String customerId) {
        // 삭제하기 전에 해당 ID의 고객이 존재하는지 먼저 확인하는 것이 더 안전한 코드입니다.
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("존재하지 않는 고객 ID입니다: " + customerId);
        }
        customerRepository.deleteById(customerId);
    }




}
