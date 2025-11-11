package gnu.project.backend.owner.dto.response;

import gnu.project.backend.owner.entity.Owner;

public record OwnerSignInResponse(
    Long id,
    String email,
    String name,
    String profileImage,
    String phoneNumber,
    String bzNumber,
    String bankAccount,
    String bzName,
    String zipCode,
    String roadAddress,
    String jibunAddress,
    String detailAddress,
    String buildingName
) {

    public static OwnerSignInResponse from(Owner owner) {
        return new OwnerSignInResponse(
            owner.getId(),
            owner.getOauthInfo() != null ? owner.getOauthInfo().getEmail() : null,
            owner.getOauthInfo() != null ? owner.getOauthInfo().getName() : null,
            owner.getProfileImage(),
            owner.getPhoneNumber(),
            owner.getBzNumber(),
            owner.getBankAccount(),
            owner.getBzName(),
            owner.getZipCode(),
            owner.getRoadAddress(),
            owner.getJibunAddress(),
            owner.getDetailAddress(),
            owner.getBuildingName()
        );
    }
}
