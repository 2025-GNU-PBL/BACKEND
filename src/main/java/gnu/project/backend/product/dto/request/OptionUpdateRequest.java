package gnu.project.backend.product.dto.request;

import static gnu.project.backend.product.constant.ProductConstant.MAX_OPTION_DETAIL_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.MAX_OPTION_NAME_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.MIN_OPTION_PRICE;
import static gnu.project.backend.product.constant.ProductConstant.OPTION_DETAIL_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.OPTION_NAME_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.OPTION_NAME_REQUIRED;
import static gnu.project.backend.product.constant.ProductConstant.OPTION_PRICE_MIN;
import static gnu.project.backend.product.constant.ProductConstant.OPTION_PRICE_REQUIRED;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OptionUpdateRequest(
    Long id,

    @NotBlank(message = OPTION_NAME_REQUIRED)
    @Size(max = MAX_OPTION_NAME_LENGTH, message = OPTION_NAME_LENGTH)
    String name,

    @NotNull(message = OPTION_PRICE_REQUIRED)
    @Min(value = MIN_OPTION_PRICE, message = OPTION_PRICE_MIN)
    Integer price,

    @Size(max = MAX_OPTION_DETAIL_LENGTH, message = OPTION_DETAIL_LENGTH)
    String detail
) {

}
