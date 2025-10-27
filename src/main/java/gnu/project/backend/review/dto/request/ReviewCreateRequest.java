package gnu.project.backend.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static gnu.project.backend.review.constant.ReviewConstant.*;

public record ReviewCreateRequest(

        @NotBlank(message = REVIEW_NAME_REQUIRED)
        String title,

        @NotNull(message = REVIEW_STAR_REQUIRED)
        @Min(value = 1, message = REVIEW_STAR_MIN)
        @Max(value = 5, message = REVIEW_STAR_MAX)
        Short star,

        String comment
) {}