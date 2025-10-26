package gnu.project.backend.product.dto.request;

import gnu.project.backend.product.constant.ProductConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OptionCreateRequest(

    @NotBlank(message = ProductConstant.OPTION_NAME_REQUIRED)
    @Size(max = ProductConstant.MAX_OPTION_NAME_LENGTH, message = ProductConstant.OPTION_NAME_LENGTH)
    String name,

    @NotNull(message = ProductConstant.OPTION_PRICE_REQUIRED)
    @Min(value = ProductConstant.MIN_OPTION_PRICE, message = ProductConstant.OPTION_PRICE_MIN)
    Integer price,

    @Size(max = ProductConstant.MAX_OPTION_DETAIL_LENGTH, message = ProductConstant.OPTION_DETAIL_LENGTH)
    String detail
) {

}
