package gnu.project.backend.customer.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

import static gnu.project.backend.customer.constant.CustomerConstant.*;

public record CustomerRequest(

        @NotBlank(message = PHONE_NUMBER_REQUIRED_MESSAGE)
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = PHONE_NUMBER_PATTERN_MESSAGE
        )
        String phoneNumber,

        @Size(max = 255, message = ADDRESS_SIZE_MESSAGE)
        String address,
        String zipCode,
        String roadAddress,
        String detailAddress,
        String buildingName,

        @Size(max = 30, message = SIDO_SIZE_MESSAGE)
        String weddingSido,
        @Size(max = 50, message = SIGUNGU_SIZE_MESSAGE)
        String weddingSigungu,

        LocalDate weddingDate

) {
}
