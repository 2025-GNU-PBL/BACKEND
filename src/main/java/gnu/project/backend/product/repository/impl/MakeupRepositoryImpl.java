package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QMakeup.makeup;
import static gnu.project.backend.product.entity.QTag.tag;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.DressResponse.TagResponse;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.MakeupTag;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.repository.MakeupCustomRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MakeupRepositoryImpl implements MakeupCustomRepository {

    private static final OrderSpecifier<?>[] MAKEUP_DEFAULT_ORDER = {
        makeup.createdAt.desc(),
        makeup.id.desc()
    };
    private final JPAQueryFactory query;

    @Override
    public MakeupResponse findByMakeupId(final Long id) {
        Makeup result = query
            .selectFrom(makeup)
            .distinct()
            .leftJoin(makeup.images, image).fetchJoin()
            .where(makeup.id.eq(id))
            .fetchOne();
        return MakeupResponse.from(Objects.requireNonNull(result));
    }

    @Override
    public List<ProductPageResponse> searchMakeup(final int pageSize, final int pageNumber) {
        return pagination(query
                .select(createMakeupResponse())
                .from(makeup)
                .leftJoin(makeup.images, image)
                .where(image.displayOrder.eq(0).or(image.isNull()))
                .orderBy(MAKEUP_DEFAULT_ORDER),
            pageSize,
            pageNumber
        ).fetch();
    }

    @Override
    public List<ProductPageResponse> searchMakeupsByFilter(List<MakeupTag> tags, Category category,
        Region region, Integer minPrice, Integer maxPrice, SortType sortType, Integer pageNumber,
        Integer pageSize) {
        OrderSpecifier<?> order = switch (sortType) {
            case PRICE_ASC -> makeup.price.asc();
            case PRICE_DESC -> makeup.price.desc();
            case LATEST -> makeup.createdAt.desc();
            default -> makeup.createdAt.desc();
        };

        List<ProductPageResponse> makeups = pagination(query
            .select(createMakeupResponse())
            .from(makeup)
            .leftJoin(makeup.images, image)
            .where(image.displayOrder.eq(0).or(image.isNull()))
            .leftJoin(makeup.tags, tag)
            .where(
                categoryEq(category),
                regionEq(region),
                priceBetween(minPrice, maxPrice),
                tagsIn(tags)
            ).orderBy(order), pageSize, pageNumber).fetch();

        if (makeups.isEmpty()) {
            return List.of();
        }

        List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(makeups.stream()
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

        return makeups.stream()
            .map(makeup -> new ProductPageResponse(
                makeup.id(),
                makeup.name(),
                makeup.starCount(),
                makeup.address(),
                makeup.detail(),
                makeup.price(),
                makeup.availableTime(),
                makeup.createdAt(),
                makeup.region(),
                makeup.Thumbnail(),
                tagsMap.getOrDefault(makeup.id(), List.of())
            ))
            .toList();
    }

    @Override
    public Long countMakeupsByFilter(List<MakeupTag> tags, Category category, Region region,
        Integer minPrice, Integer maxPrice) {
        return query.select(makeup.countDistinct())
            .from(makeup)
            .leftJoin(makeup.tags, tag)
            .where(
                categoryEq(category),
                regionEq(region),
                priceBetween(minPrice, maxPrice),
                tagsIn(tags)
            ).fetchOne();
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? makeup.category.eq(category) : null;
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? makeup.region.eq(region) : null;
    }

    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return makeup.price.loe(maxPrice);
        }
        if (maxPrice == null) {
            return makeup.price.goe(minPrice);
        }
        return makeup.price.between(minPrice, maxPrice);
    }

    private BooleanExpression tagsIn(List<MakeupTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tag.name.in(
            tags.stream()
                .map(MakeupTag::name)
                .toList()
        );
    }

    private ConstructorExpression<ProductPageResponse> createMakeupResponse() {
        return Projections.constructor(
            ProductPageResponse.class,
            makeup.id,
            makeup.name,
            makeup.starCount,
            makeup.address,
            makeup.detail,
            makeup.price,
            makeup.availableTimes,
            makeup.createdAt,
            makeup.region,
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