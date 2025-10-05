package gnu.project.backend.product.provider;

import gnu.project.backend.product.dto.request.OptionCreateRequest;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.entity.Product;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OptionProvider {

    public void createOptions(
        final Product product,
        final List<OptionCreateRequest> options
    ) {
        List<Option> optionList = new ArrayList<>();
        for (OptionCreateRequest req : options) {
            optionList.add(Option.ofCreate(
                    product,
                    req.detail(),
                    req.price(),
                    req.name()
                )
            );
        }
        product.addAllOption(optionList);
    }
}
