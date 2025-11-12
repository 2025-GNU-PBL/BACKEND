package gnu.project.backend.customer.dto.response;

import gnu.project.backend.customer.entity.Customer;

import java.time.LocalDate;

public record CustomerUpdateResponse(
        Long id,
        String name,
        String email,
        String socialId,
        String phoneNumber,
        String address,

        String zipCode,
        String roadAddress,
        String jibunAddress,
        String detailAddress,
        String sido,
        String sigungu,
        String dong,
        String buildingName,

        String weddingSido,
        String weddingSigungu,
        LocalDate weddingDate
) {
    public static CustomerUpdateResponse from(final Customer customer) {
        return new CustomerUpdateResponse(
                customer.getId(),
                customer.getOauthInfo().getName(),
                customer.getOauthInfo().getEmail(),
                customer.getOauthInfo().getSocialId(),
                customer.getPhoneNumber(),
                customer.getAddress(),

                customer.getZipCode(),
                customer.getRoadAddress(),
                customer.getJibunAddress(),
                customer.getDetailAddress(),
                customer.getSido(),
                customer.getSigungu(),
                customer.getDong(),
                customer.getBuildingName(),

                customer.getWeddingSido(),
                customer.getWeddingSigungu(),
                customer.getWeddingDate()
        );
    }
}
