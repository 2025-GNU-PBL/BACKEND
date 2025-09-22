package gnu.project.backend.owner.dto.request;

import static gnu.project.backend.owner.constant.OwnerConstant.AGE_MAX_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.AGE_MIN_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.AGE_REQUIRED_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BANK_ACCOUNT_PATTERN_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BANK_ACCOUNT_REQUIRED_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BANK_ACCOUNT_SIZE_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BZ_NUMBER_PATTERN_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.BZ_NUMBER_REQUIRED_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.PHONE_NUMBER_PATTERN_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.PHONE_NUMBER_REQUIRED_MESSAGE;
import static gnu.project.backend.owner.constant.OwnerConstant.PROFILE_IMAGE_SIZE_MESSAGE;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OwnerRequest(
    @Size(max = 500, message = PROFILE_IMAGE_SIZE_MESSAGE)
    String profileImage,

    @NotNull(message = AGE_REQUIRED_MESSAGE)
    @Min(value = 18, message = AGE_MIN_MESSAGE)
    @Max(value = 120, message = AGE_MAX_MESSAGE)
    Short age,

    @NotBlank(message = PHONE_NUMBER_REQUIRED_MESSAGE)
    @Pattern(
        regexp = "^010-\\d{4}-\\d{4}$",
        message = PHONE_NUMBER_PATTERN_MESSAGE
    )
    String phoneNumber,

    @NotBlank(message = BZ_NUMBER_REQUIRED_MESSAGE)
    @Pattern(
        regexp = "^\\d{3}-\\d{2}-\\d{5}$",
        message = BZ_NUMBER_PATTERN_MESSAGE
    )
    String bzNumber,

    @NotBlank(message = BANK_ACCOUNT_REQUIRED_MESSAGE)
    @Pattern(
        regexp = "^[0-9-]+$",
        message = BANK_ACCOUNT_PATTERN_MESSAGE
    )
    @Size(min = 10, max = 20, message = BANK_ACCOUNT_SIZE_MESSAGE)
    String bankAccount
) {

}
