package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QDress.dress;
import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QOption.option;
import static gnu.project.backend.product.entity.QStudio.studio;
import static gnu.project.backend.product.entity.QTag.tag;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.DressResponse.TagResponse;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import gnu.project.backend.product.entity.Studio;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.StudioTag;
import gnu.project.backend.product.repository.StudioCustomRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudioRepositoryImpl implements StudioCustomRepository {

    private static final OrderSpecifier<?>[] STUDIO_DEFAULT_ORDER = {
        studio.id.desc(),
        studio.createdAt.desc()
    };
    private final JPAQueryFactory query;

    @Override
    public StudioResponse findByStudioId(final Long id) {
        Studio result = query
            .selectFrom(studio)
            .distinct()
            .leftJoin(studio.images, image).fetchJoin()
            .leftJoin(studio.tags, tag)
            .where(studio.id.eq(id))
            .fetchOne();
        return StudioResponse.from(Objects.requireNonNull(result));
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? studio.region.eq(region) : null;
    }

    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return studio.price.loe(maxPrice);
        }
        if (maxPrice == null) {
            return studio.price.goe(minPrice);
        }
        return studio.price.between(minPrice, maxPrice);
    }

    private BooleanExpression tagsIn(List<StudioTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tag.name.in(tags.stream().map(StudioTag::name).toList());
    }

    public Long countStudiosByFilter(
        List<StudioTag> tags,
        Region region,
        Integer minPrice,
        Integer maxPrice
    ) {
        return query
            .select(studio.countDistinct())
            .from(studio)
            .leftJoin(studio.tags, tag)
            .where(
                regionEq(region),
                priceBetween(minPrice, maxPrice),
                tagsIn(tags)
            )
            .fetchOne();
    }

    @Override
    public List<ProductPageResponse> searchStudio(int pageSize, int pageNumber) {
        List<ProductPageResponse> studios = pagination(
            query
                .select(createStudioResponse())
                .from(studio)
                .leftJoin(studio.images, image)
                .where(image.displayOrder.eq(0).or(image.isNull()))
                .orderBy(STUDIO_DEFAULT_ORDER),
            pageSize,
            pageNumber
        ).fetch();

        if (studios.isEmpty()) {
            return List.of();
        }

        List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(studios.stream()
                .map(ProductPageResponse::id)
                .toList())
            ).fetch();

        Map<Long, List<TagResponse>> tagsMap = allTags.stream()
            .collect(Collectors.groupingBy(
                t -> t.getProduct().getId(),
                Collectors.mapping(
                    t -> new TagResponse(t.getId(), t.getName()),
                    Collectors.toList()
                )
            ));

        return studios.stream()
            .map(studio -> new ProductPageResponse(
                studio.id(),
                studio.name(),
                studio.starCount(),
                studio.address(),
                studio.detail(),
                studio.price(),
                studio.availableTime(),
                studio.createdAt(),
                studio.region(),
                studio.Thumbnail(),
                tagsMap.getOrDefault(studio.id(), List.of())
            ))
            .toList();
    }

    @Override
    public Optional<Studio> findStudioWithImagesAndOptionsById(Long id) {
        return Optional.ofNullable(
            query
                .selectFrom(studio)
                .leftJoin(studio.images, image).fetchJoin()
                .leftJoin(studio.options, option)
                .leftJoin(studio.tags, tag)
                .where(studio.id.eq(id))
                .fetchOne()
        );
    }

    @Override
    public List<ProductPageResponse> searchStudiosByFilter(List<StudioTag> tags, Category category,
        Region region, Integer minPrice, Integer maxPrice, SortType sortType, Integer pageNumber,
        Integer pageSize) {
        OrderSpecifier<?> order = switch (sortType) {
            case PRICE_ASC -> studio.price.asc();
            case PRICE_DESC -> studio.price.desc();
            case LATEST -> studio.createdAt.desc();
            default -> studio.createdAt.desc();
        };

        List<ProductPageResponse> studios = pagination(
            query
                .select(createStudioResponse())
                .from(studio)
                .leftJoin(studio.images, image)
                .where(image.displayOrder.eq(0).or(image.isNull()))
                .leftJoin(studio.tags, tag)
                .where(
                    regionEq(region),
                    priceBetween(minPrice, maxPrice),
                    tagsIn(tags)
                )
                .orderBy(order),
            pageSize,
            pageNumber
        ).fetch();

        if (studios.isEmpty()) {
            return List.of();
        }

        List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(studios.stream()
                .map(ProductPageResponse::id)
                .toList())
            ).fetch();

        Map<Long, List<TagResponse>> tagsMap = allTags.stream()
            .collect(Collectors.groupingBy(
                t -> t.getProduct().getId(),
                Collectors.mapping(
                    t -> new TagResponse(t.getId(), t.getName()),
                    Collectors.toList()
                )
            ));

        return studios.stream()
            .map(studio -> new ProductPageResponse(
                studio.id(),
                studio.name(),
                studio.starCount(),
                studio.address(),
                studio.detail(),
                studio.price(),
                studio.availableTime(),
                studio.createdAt(),
                studio.region(),
                studio.Thumbnail(),
                tagsMap.getOrDefault(studio.id(), List.of())
            ))
            .toList();
    }

    @Override
    public Long countStudiosByFilter(List<StudioTag> tags, Category category, Region region,
        Integer minPrice, Integer maxPrice) {
        return query.select(dress.countDistinct())
            .from(dress)
            .leftJoin(dress.tags, tag)
            .where(
                categoryEq(category),
                regionEq(region),
                priceBetween(minPrice, maxPrice),
                tagsIn(tags)
            ).fetchOne();
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? dress.category.eq(category) : null;
    }

    private ConstructorExpression<ProductPageResponse> createStudioResponse() {
        return Projections.constructor(
            ProductPageResponse.class,
            studio.id,
            studio.name,
            studio.starCount,
            studio.address,
            studio.detail,
            studio.price,
            studio.availableTimes,
            studio.createdAt,
            studio.region,
            image.url,
            Expressions.nullExpression(List.class)
        );
    }

    private <T> JPAQuery<T> pagination(
        final JPAQuery<T> query,
        final Integer pageSize,
        final Integer pageNumber
    ) {
        return query
            .offset((long) (pageNumber - 1) * pageSize)
            .limit(pageSize);
    }
}
