package gnu.project.backend.customer.dto.response;

import gnu.project.backend.customer.entity.Customer;

public record CustomerUpdateResponse(
        Long id,
        String name,
        String email,
        String socialId,
        Short age,
        String phoneNumber,
        String address
) {
    public static CustomerUpdateResponse from(final Customer customer) {
        return new CustomerUpdateResponse(
                customer.getId(),
                customer.getOauthInfo().getName(),
                customer.getOauthInfo().getEmail(),
                customer.getOauthInfo().getSocialId(),
                customer.getAge(),
                customer.getPhoneNumber(),
                customer.getAddress()
        );
    }
}
