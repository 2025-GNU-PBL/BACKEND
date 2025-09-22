package gnu.project.backend.owner.dto.response;

import gnu.project.backend.owner.entity.Owner;

public record OwnerSignInResponse(
    Long id,
    String email,
    String name,
    String profileImage,
    Short age,
    String phoneNumber,
    String bzNumber,
    String bankAccount,
    String bzName
) {

    public static OwnerSignInResponse from(Owner owner) {
        return new OwnerSignInResponse(
            owner.getId(),
            owner.getEmail(),
            owner.getName(),
            owner.getProfileImage(),
            owner.getAge(),
            owner.getPhoneNumber(),
            owner.getBzNumber(),
            owner.getBankAccount(),
            owner.getBzName()
        );
    }

}
