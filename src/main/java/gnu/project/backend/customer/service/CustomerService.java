package gnu.project.backend.customer.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer read(final Accessor accessor) {
        return customerRepository.
                findByOauthInfo_SocialId(accessor.getSocialId())
                //TODO : CUSTOMER EXCEPTION 추가 후 적용
            .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));
    }




}
