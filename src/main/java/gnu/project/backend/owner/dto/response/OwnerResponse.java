package gnu.project.backend.owner.dto.response;

import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.owner.entity.Owner;
import java.time.LocalDateTime;

public record OwnerResponse(
    Long id,
    String profileImage,
    Short age,
    String phoneNumber,
    String bzNumber,
    String bankAccount,
    UserRole userRole,

    // OAuth 정보
    String email,
    String name,
    String socialId,
    SocialProvider socialProvider,

    // BaseEntity 정보
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static OwnerResponse from(Owner owner) {
        return new OwnerResponse(
            owner.getId(),
            owner.getProfileImage(),
            owner.getAge(),
            owner.getPhoneNumber(),
            owner.getBzNumber(),
            owner.getBankAccount(),
            owner.getUserRole(),

            // OAuth 정보 추출
            owner.getOauthInfo().getEmail(),
            owner.getOauthInfo().getName(),
            owner.getOauthInfo().getSocialId(),
            owner.getOauthInfo().getSocialProvider(),

            // BaseEntity 정보 추출
            owner.getCreatedAt(),
            owner.getUpdatedAt()
        );
    }
}
