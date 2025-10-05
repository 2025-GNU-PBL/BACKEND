package gnu.project.backend.product.provider;

import gnu.project.backend.product.dto.request.TagRequest;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.entity.Tag;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TagProvider {

    public void createTag(
        final Product product,
        final List<TagRequest> tagRequest
    ) {
        final List<Tag> tags = new ArrayList<>();
        for (final TagRequest request : tagRequest) {
            tags.add(Tag.ofCreate(product, request.tagName()));
        }
        product.addAllTag(tags);
    }
}
