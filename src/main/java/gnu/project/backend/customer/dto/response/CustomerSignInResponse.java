package gnu.project.backend.customer.dto.response;

import gnu.project.backend.customer.entity.Customer;

import java.time.LocalDate;

public record CustomerSignInResponse(
        Long id,
        String name,
        String email,
        String socialId,
        String phoneNumber,
        String address,

        String zipCode,
        String roadAddress,
        String detailAddress,
        String buildingName,

        String weddingSido,
        String weddingSigungu,
        LocalDate weddingDate
) {
    public static CustomerSignInResponse from(final Customer customer) {
        return new CustomerSignInResponse(
                customer.getId(),
                customer.getOauthInfo().getName(),
                customer.getOauthInfo().getEmail(),
                customer.getOauthInfo().getSocialId(),
                customer.getPhoneNumber(),
                customer.getAddress(),

                customer.getZipCode(),
                customer.getRoadAddress(),
                customer.getDetailAddress(),
                customer.getBuildingName(),

                customer.getWeddingSido(),
                customer.getWeddingSigungu(),
                customer.getWeddingDate()
        );
    }
}
