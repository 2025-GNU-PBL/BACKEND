package gnu.project.backend.customer.dto.response;

import gnu.project.backend.customer.entity.Customer;

public record CustomerSignInResponse(
        Long id,
        String name,
        String email,
        String socialId,
        Short age,
        String phoneNumber,
        String address
) {
    public static CustomerSignInResponse from(final Customer customer) {
        return new CustomerSignInResponse(
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
