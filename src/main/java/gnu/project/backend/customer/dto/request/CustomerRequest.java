package gnu.project.backend.customer.dto.request;

import jakarta.validation.constraints.*;

import static gnu.project.backend.customer.constant.CustomerConstant.*;

public record CustomerRequest(

        @NotNull(message = AGE_REQUIRED_MESSAGE)
        @Max(value = 120, message = AGE_MAX_MESSAGE)
        Short age,

        @NotBlank(message = PHONE_NUMBER_REQUIRED_MESSAGE)
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = PHONE_NUMBER_PATTERN_MESSAGE
        )
        String phoneNumber,

        @Size(max = 255, message = ADDRESS_SIZE_MESSAGE)
        String address

) {
}
