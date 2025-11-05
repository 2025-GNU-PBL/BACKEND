package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QDress.dress;
import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QOption.option;
import static gnu.project.backend.product.entity.QTag.tag;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.DressPageResponse;
import gnu.project.backend.product.dto.response.DressResponse;
import gnu.project.backend.product.dto.response.DressResponse.TagResponse;
import gnu.project.backend.product.entity.Dress;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.DressTag;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.repository.DressCustomRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DressRepositoryImpl implements DressCustomRepository {

    private static final OrderSpecifier<?>[] DRESS_DEFAULT_ORDER = {
        dress.id.desc(),
        dress.createdAt.desc()
    };
    private final JPAQueryFactory query;

    @Override
    public DressResponse findByDressId(final Long id) {
        Dress result = query
            .selectFrom(dress)
            .distinct()
            .leftJoin(dress.images, image).fetchJoin()
            .leftJoin(dress.tags, tag)
            .where(dress.id.eq(id))
            .fetchOne();
        return DressResponse.from(Objects.requireNonNull(result));
    }

    @Override
    public List<DressPageResponse> searchDress(int pageSize, int pageNumber) {
        List<DressPageResponse> dresses = pagination(
            query
                .select(createDressResponse())
                .from(dress)
                .leftJoin(dress.images, image)
                .where(image.displayOrder.eq(0).or(image.isNull()))
                .orderBy(DRESS_DEFAULT_ORDER),
            pageSize,
            pageNumber
        ).fetch();

        if (dresses.isEmpty()) {
            return List.of();
        }

        List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(dresses.stream()
                .map(DressPageResponse::id)
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

        return dresses.stream()
            .map(dress -> new DressPageResponse(
                dress.id(),
                dress.name(),
                dress.starCount(),
                dress.address(),
                dress.detail(),
                dress.price(),
                dress.availableTime(),
                dress.createdAt(),
                dress.Thumbnail(),
                dress.region(),
                tagsMap.getOrDefault(dress.id(), List.of())
            ))
            .toList();
    }

    @Override
    public Optional<Dress> findDressWithImagesAndOptionsById(Long id) {
        return Optional.ofNullable(
            query
                .selectFrom(dress)
                .leftJoin(dress.images, image).fetchJoin()
                .leftJoin(dress.options, option)
                .leftJoin(dress.tags, tag)
                .where(dress.id.eq(id))
                .fetchOne()
        );
    }

    @Override
    public List<DressPageResponse> searchDressByFilter(List<DressTag> tags,
        Category category, Region region, Integer minPrice,
        Integer maxPrice, SortType sortType, Integer pageNumber,
        Integer pageSize) {

        OrderSpecifier<?> order = switch (sortType) {
            case PRICE_ASC -> dress.price.asc();
            case PRICE_DESC -> dress.price.desc();
            case LATEST -> dress.createdAt.desc();
            default -> dress.createdAt.desc();
        };

        List<DressPageResponse> dresses = pagination(query
            .select(createDressResponse())
            .from(dress)
            .leftJoin(dress.images, image)
            .where(image.displayOrder.eq(0).or(image.isNull()))
            .leftJoin(dress.tags, tag)
            .where(
                categoryEq(category),
                regionEq(region),
                priceBetween(minPrice, maxPrice),
                tagsIn(tags)
            ).orderBy(order), pageSize, pageNumber).fetch();

        if (dresses.isEmpty()) {
            return List.of();
        }

        List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(dresses.stream()
                .map(DressPageResponse::id)
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

        return dresses.stream()
            .map(dress -> new DressPageResponse(
                dress.id(),
                dress.name(),
                dress.starCount(),
                dress.address(),
                dress.detail(),
                dress.price(),
                dress.availableTime(),
                dress.createdAt(),
                dress.Thumbnail(),
                dress.region(),
                tagsMap.getOrDefault(dress.id(), List.of())
            ))
            .toList();
    }

    @Override
    public Long countDressByFilter(List<DressTag> tags, Category category,
        Region region, Integer minPrice, Integer maxPrice) {
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

    private ConstructorExpression<DressPageResponse> createDressResponse() {
        return Projections.constructor(
            DressPageResponse.class,
            dress.id,
            dress.name,
            dress.starCount,
            dress.address,
            dress.detail,
            dress.price,
            dress.availableTimes,
            dress.createdAt,
            image.url,
            dress.region,
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

    private BooleanExpression categoryEq(Category category) {
        return category != null ? dress.category.eq(category) : null;
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? dress.region.eq(region) : null;
    }

    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return dress.price.loe(maxPrice);
        }
        if (maxPrice == null) {
            return dress.price.goe(minPrice);
        }
        return dress.price.between(minPrice, maxPrice);
    }

    private BooleanExpression tagsIn(List<DressTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tag.name.in(
            tags.stream()
                .map(DressTag::name)
                .toList()
        );
    }
}
