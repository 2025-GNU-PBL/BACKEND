package gnu.project.backend.owner.dto.request;

import static gnu.project.backend.owner.constant.OwnerConstant.BANK_ACCOUNT_PATTERN_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BANK_ACCOUNT_REQUIRED_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BANK_ACCOUNT_SIZE_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BIZ_NAME_REQUIRED_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BZ_NUMBER_REQUIRED_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.PHONE_NUMBER_REQUIRED_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.PROFILE_IMAGE_SIZE_MESSAGE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OwnerRequest(
    @Size(max = 500, message = PROFILE_IMAGE_SIZE_MESSAGE)
    String profileImage,

    @NotBlank(message = PHONE_NUMBER_REQUIRED_MESSAGE)
    String phoneNumber,

    @NotBlank(message = BZ_NUMBER_REQUIRED_MESSAGE)
    String bzNumber,

    @NotBlank(message = BANK_ACCOUNT_REQUIRED_MESSAGE)
    @Pattern(
        regexp = "^[0-9-]+$",
        message = BANK_ACCOUNT_PATTERN_MESSAGE
    )
    @Size(min = 10, max = 20, message = BANK_ACCOUNT_SIZE_MESSAGE)
    String bankAccount,

    @NotBlank(message = BIZ_NAME_REQUIRED_MESSAGE)
    String bzName,
    String zipCode,
    String roadAddress,
    String jibunAddress,
    String detailAddress,
    String buildingName,
    String bankName
) {

}
