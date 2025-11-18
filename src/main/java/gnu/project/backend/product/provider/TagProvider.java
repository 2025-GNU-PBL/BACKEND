package gnu.project.backend.product.provider;

import gnu.project.backend.product.dto.request.TagRequest;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.DressTag;
import gnu.project.backend.product.enumerated.MakeupTag;
import gnu.project.backend.product.enumerated.StudioTag;
import gnu.project.backend.product.enumerated.WeddingHallTag;
import gnu.project.backend.product.repository.TagRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagProvider {

    private final TagRepository tagRepository;

    public static boolean isValidForProduct(final Product product, final String tagName) {
        Category category = product.getCategory();
        return switch (category) {
            case DRESS -> isEnumValue(DressTag.class, tagName);
            case STUDIO -> isEnumValue(StudioTag.class, tagName);
            case WEDDING_HALL -> isEnumValue(WeddingHallTag.class, tagName);
            case MAKEUP -> isEnumValue(MakeupTag.class, tagName);
        };
    }

    private static <E extends Enum<E>> boolean isEnumValue(Class<E> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
            .anyMatch(e -> e.name().equalsIgnoreCase(name));
    }

    public void createTag(
        final Product product,
        final List<TagRequest> tagRequests
    ) {
        if (tagRequests == null || tagRequests.isEmpty()) {
            return;
        }

        final List<Tag> validTags = tagRequests.stream()
            .filter(req -> isValidForProduct(product, req.tagName()))
            .map(req -> Tag.ofCreate(product, req.tagName()))
            .collect(Collectors.toList());

        product.addAllTag(validTags);
        tagRepository.saveAll(validTags);
    }

    public void updateTags(final Product product, final List<TagRequest> newTagRequests) {
        final List<Tag> currentTags = new ArrayList<>(product.getTags());

        final Set<String> newTagNames = newTagRequests.stream()
            .map(TagRequest::tagName)
            .collect(Collectors.toSet());

        final List<Tag> tagsToRemove = currentTags.stream()
            .filter(tag -> !newTagNames.contains(tag.getName()))
            .collect(Collectors.toList());

        final Set<String> existingTagNames = currentTags.stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());

        final List<Tag> tagsToAdd = newTagRequests.stream()
            .filter(req -> !existingTagNames.contains(req.tagName()))
            .map(req -> Tag.ofCreate(product, req.tagName()))
            .collect(Collectors.toList());

        tagsToRemove.forEach(product::removeTag);
        tagRepository.deleteAll(tagsToRemove);

        product.addAllTag(tagsToAdd);
        tagRepository.saveAll(tagsToAdd);
    }
}
