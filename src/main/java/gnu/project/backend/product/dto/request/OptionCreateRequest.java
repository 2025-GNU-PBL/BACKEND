package gnu.project.backend.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OptionCreateRequest(
    @NotBlank(message = "옵션명은 필수입니다")
    @Size(max = 50, message = "옵션명은 50자 이하여야 합니다")
    String name,

    @NotNull(message = "옵션 가격은 필수입니다")
    @Min(value = 0, message = "옵션 가격은 0 이상이어야 합니다")
    Integer price,

    @Size(max = 200, message = "옵션 설명은 200자 이하여야 합니다")
    String detail
) {

}
