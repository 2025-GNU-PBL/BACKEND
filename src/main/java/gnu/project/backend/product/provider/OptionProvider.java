package gnu.project.backend.product.provider;

import gnu.project.backend.product.dto.request.OptionCreateRequest;
import gnu.project.backend.product.dto.request.OptionUpdateRequest;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.OptionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptionProvider {

    private final OptionRepository optionRepository;

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
        optionRepository.saveAll(optionList);
    }

    public void updateOptions(
        final Product product,
        final List<OptionUpdateRequest> optionUpdateRequests
    ) {
        final Map<Long, Option> existingOptionsMap = product.getOptions().stream()
            .collect(Collectors.toMap(Option::getId, Function.identity()));

        final List<Option> finalOptions = new ArrayList<>();

        for (OptionUpdateRequest req : optionUpdateRequests) {
            Option option;
            if (req.id() != null && existingOptionsMap.containsKey(req.id())) {
                option = existingOptionsMap.get(req.id());
                option.update(req.name(), req.detail(), req.price());
            } else {
                option = Option.ofCreate(product, req.detail(), req.price(), req.name());
            }
            finalOptions.add(option);
        }
        product.getOptions().clear();
        product.getOptions().addAll(finalOptions);
        
        optionRepository.saveAll(finalOptions);
    }
}
