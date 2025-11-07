package gnu.project.backend.customer.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.dto.request.CustomerRequest;
import gnu.project.backend.customer.dto.response.CustomerResponse;
import gnu.project.backend.customer.dto.response.CustomerSignInResponse;
import gnu.project.backend.customer.dto.response.CustomerUpdateResponse;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_DELETED_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerResponse findCustomer(final Accessor accessor) {

        final Customer customer = findCustomerBySocialId(accessor);

        if (!customer.isActive()) {
            throw new BusinessException(CUSTOMER_DELETED_EXCEPTION);
        }
        return CustomerResponse.from(customer);
    }


    public CustomerSignInResponse signUp(
            final Accessor accessor,
            final CustomerRequest request
    ) {
        final Customer customer = findCustomerBySocialId(accessor);

        if (!customer.isActive()) {
            customer.reactivate();
        }

        customer.signUp(
                request.age(),
                request.phoneNumber(),
                request.address(),
                request.zipCode(),
                request.roadAddress(),
                request.jibunAddress(),
                request.detailAddress(),
                request.sido(),
                request.sigungu(),
                request.dong(),
                request.buildingName(),
                request.weddingSido(),
                request.weddingSigungu(),
                request.weddingDate()
        );

        return CustomerSignInResponse.from(customer);
    }


    public CustomerUpdateResponse update(
            final Accessor accessor,
            final CustomerRequest request
    ) {
        final Customer customer = findCustomerBySocialId(accessor);

        if (!customer.isActive()) {
            throw new BusinessException(CUSTOMER_DELETED_EXCEPTION);
        }

        customer.updateProfile(
                request.age(),
                request.phoneNumber(),
                request.address(),
                request.zipCode(),
                request.roadAddress(),
                request.jibunAddress(),
                request.detailAddress(),
                request.sido(),
                request.sigungu(),
                request.dong(),
                request.buildingName(),
                request.weddingSido(),
                request.weddingSigungu(),
                request.weddingDate()
        );

        return CustomerUpdateResponse.from(customer);
    }

    public void withdraw(final Accessor accessor) {
        final Customer customer = findCustomerBySocialId(accessor);

        if (customer.isActive()) {
            customer.withdraw();
        }
    }

    private Customer findCustomerBySocialId(Accessor accessor) {
        return customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));
    }


}
