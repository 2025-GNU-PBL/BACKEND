package gnu.project.backend.owner.dto.response;

import gnu.project.backend.auth.enumerated.SocialProvider;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.owner.entity.Owner;
import java.time.LocalDateTime;

public record OwnerResponse(
    Long id,
    String profileImage,
    String phoneNumber,
    String bzNumber,
    String bankAccount,
    UserRole userRole,
    String email,
    String name,
    String socialId,
    SocialProvider socialProvider,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String bzName,
    String zipCode,
    String roadAddress,
    String jibunAddress,
    String detailAddress,
    String buildingName,
    String bankName
) {

    public static OwnerResponse from(Owner owner) {
        return new OwnerResponse(
            owner.getId(),
            owner.getProfileImage(),
            owner.getPhoneNumber(),
            owner.getBzNumber(),
            owner.getBankAccount(),
            owner.getUserRole(),
            owner.getOauthInfo().getEmail(),
            owner.getOauthInfo().getName(),
            owner.getOauthInfo().getSocialId(),
            owner.getOauthInfo().getSocialProvider(),
            owner.getCreatedAt(),
            owner.getUpdatedAt(),
            owner.getBzName(),
            owner.getZipCode(),
            owner.getRoadAddress(),
            owner.getJibunAddress(),
            owner.getDetailAddress(),
            owner.getBuildingName(),
            owner.getBankName()
        );
    }
}
