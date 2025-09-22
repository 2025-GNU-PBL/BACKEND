package gnu.project.backend.owner.dto.request;

public record OwnerSignInRequest(
    String profileImage,

    Short age,

    String phoneNumber,

    String bzNumber,

    String bankAccount
) {

}
