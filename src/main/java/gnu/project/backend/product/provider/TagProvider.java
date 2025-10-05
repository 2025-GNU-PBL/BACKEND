package gnu.project.backend.product.provider;

import gnu.project.backend.product.dto.request.TagRequest;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.repository.TagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagProvider {

    private final TagRepository tagRepository;

    public void createTag(
        final Product product,
        final List<TagRequest> tagRequest
    ) {
        final List<Tag> tags = new ArrayList<>();
        for (final TagRequest request : tagRequest) {
            tags.add(Tag.ofCreate(product, request.tagName()));
        }
        product.addAllTag(tags);
        tagRepository.saveAll(tags);
    }
}
