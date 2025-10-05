package gnu.project.backend.product.dto.request;

import gnu.project.backend.product.constant.MakeupConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OptionCreateRequest(

    @NotBlank(message = MakeupConstant.OPTION_NAME_REQUIRED)
    @Size(max = MakeupConstant.MAX_OPTION_NAME_LENGTH, message = MakeupConstant.OPTION_NAME_LENGTH)
    String name,

    @NotNull(message = MakeupConstant.OPTION_PRICE_REQUIRED)
    @Min(value = MakeupConstant.MIN_OPTION_PRICE, message = MakeupConstant.OPTION_PRICE_MIN)
    Integer price,

    @Size(max = MakeupConstant.MAX_OPTION_DETAIL_LENGTH, message = MakeupConstant.OPTION_DETAIL_LENGTH)
    String detail
) {

}
