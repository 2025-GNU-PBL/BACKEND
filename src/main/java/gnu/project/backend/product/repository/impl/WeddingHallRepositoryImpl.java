package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QTag.tag;
import static gnu.project.backend.product.entity.QWeddingHall.weddingHall;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.dto.response.TagResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.WeddingHallTag;
import gnu.project.backend.product.repository.WeddingHallCustomRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeddingHallRepositoryImpl implements WeddingHallCustomRepository {

    // 최신순 기본 정렬(updatedAt DESC → id DESC)
    private static final OrderSpecifier<?>[] DEFAULT_LATEST_ORDER = {
        weddingHall.createdAt.desc(),
        weddingHall.id.desc()
    };

    private final JPAQueryFactory query;

    @Override
    public WeddingHallResponse findByWeddingHallId(final Long id) {
        final WeddingHall hall = query
            .selectFrom(weddingHall)
            .leftJoin(weddingHall.images, image).fetchJoin() // 이미지만 fetch-join
            .distinct()
            .where(
                weddingHall.id.eq(id),
                weddingHall.isDeleted.isFalse()
            )
            .fetchOne();

        return (hall == null) ? null : WeddingHallResponse.from(hall);
    }

    /**
     * 오너 전용 목록(최신순)
     */
    @Override
    public Page<ProductPageResponse> searchWeddingHallByOwner(
        final String ownerSocialId,
        final Pageable pageable
    ) {
        final List<ProductPageResponse> rows = paginate(
            query
                .selectDistinct(createCardProjection())
                .from(weddingHall)
                .leftJoin(weddingHall.images, image)
                .on(image.id.eq(weddingHall.id).and(image.displayOrder.eq(0)))
                .where(
                    weddingHall.isDeleted.isFalse(),
                    weddingHall.owner.oauthInfo.socialId.eq(ownerSocialId)
                )
                .orderBy(DEFAULT_LATEST_ORDER),
            pageable
        ).fetch();

        if (rows.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        final Map<Long, List<TagResponse>> tagsMap = loadTagsGroupedByProductId(
            rows.stream().map(ProductPageResponse::id).toList()
        );

        final List<ProductPageResponse> withTags = rows.stream()
            .map(r -> new ProductPageResponse(
                r.id(), r.name(), r.starCount(), r.address(), r.detail(),
                r.price(), r.availableTime(), r.createdAt(), r.region(), r.thumbnail(),
                r.category(), r.bzName(),
                tagsMap.getOrDefault(r.id(), List.of())
            ))
            .toList();

        final long total = countActiveByOwner(ownerSocialId);
        return new PageImpl<>(withTags, pageable, total);
    }

    @Override
    public Optional<WeddingHall> findWeddingHallWithImagesAndOptionsById(final Long id) {
        return Optional.ofNullable(
            query
                .selectFrom(weddingHall)
                .leftJoin(weddingHall.images, image).fetchJoin()
                .where(
                    weddingHall.id.eq(id),
                    weddingHall.isDeleted.isFalse()
                )
                .fetchOne()
        );
    }

    @Override
    public long countActive() {
        final Long cnt = query
            .select(weddingHall.count())
            .from(weddingHall)
            .where(weddingHall.isDeleted.isFalse())
            .fetchOne();
        return (cnt == null) ? 0L : cnt;
    }

    @Override
    public long countActiveByOwner(final String ownerSocialId) {
        final Long cnt = query
            .select(weddingHall.count())
            .from(weddingHall)
            .where(
                weddingHall.isDeleted.isFalse(),
                weddingHall.owner.oauthInfo.socialId.eq(ownerSocialId)
            )
            .fetchOne();
        return (cnt == null) ? 0L : cnt;
    }

    @Override
    public List<ProductPageResponse> searchWeddingHallByFilter(
        final List<WeddingHallTag> tags,
        final Category category,
        final Region region,
        final Integer minPrice,
        final Integer maxPrice,
        final SortType sortType,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        final OrderSpecifier<?>[] order = toOrder(sortType);

        final List<ProductPageResponse> rows = paginate(
            query
                .selectDistinct(createCardProjection())
                .from(weddingHall)
                .leftJoin(weddingHall.images, image)
                .leftJoin(weddingHall.tags, tag)
                .where(
                    categoryEq(category),
                    regionEq(region),
                    priceBetween(minPrice, maxPrice),
                    tagsOr(tags),
                    weddingHall.isDeleted.eq(false),
                    image.displayOrder.eq(0).or(image.isNull())
                )
                .orderBy(order),
            pageSize, pageNumber
        ).fetch();

        if (rows.isEmpty()) {
            return List.of();
        }

        final Map<Long, List<TagResponse>> tagsMap = loadTagsGroupedByProductId(
            rows.stream().map(ProductPageResponse::id).toList()
        );

        return rows.stream()
            .map(r -> new ProductPageResponse(
                r.id(), r.name(), r.starCount(), r.address(), r.detail(),
                r.price(), r.availableTime(), r.createdAt(),
                r.region(),
                r.thumbnail(),
                r.category(),
                r.bzName(),
                tagsMap.getOrDefault(r.id(), List.of())
            ))
            .toList();
    }

    @Override
    public Long countWeddingHallByFilter(
        final List<WeddingHallTag> tags,
        final Category category,
        final Region region,
        final Integer minPrice,
        final Integer maxPrice
    ) {
        return query
            .select(weddingHall.countDistinct())
            .from(weddingHall)
            .leftJoin(weddingHall.tags, tag)
            .where(
                categoryEq(category),
                regionEq(region),
                priceBetween(minPrice, maxPrice),
                tagsOr(tags),
                weddingHall.isDeleted.eq(false)
            )
            .fetchOne();
    }


    private ConstructorExpression<ProductPageResponse> createCardProjection() {
        return Projections.constructor(
            ProductPageResponse.class,
            weddingHall.id,
            weddingHall.name,
            weddingHall.starCount,
            weddingHall.address,
            weddingHall.detail,
            weddingHall.price,
            weddingHall.availableTimes,
            weddingHall.createdAt,
            weddingHall.region,
            image.url,
            weddingHall.category,
            weddingHall.owner.bzName,
            Expressions.nullExpression(List.class)
        );
    }

    private BooleanExpression categoryEq(final Category category) {
        return (category != null) ? weddingHall.category.eq(category) : null;
    }

    private BooleanExpression regionEq(final Region region) {
        return (region != null) ? weddingHall.region.eq(region) : null;
    }

    private BooleanExpression priceBetween(final Integer minPrice, final Integer maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return weddingHall.price.loe(maxPrice);
        }
        if (maxPrice == null) {
            return weddingHall.price.goe(minPrice);
        }
        return weddingHall.price.between(minPrice, maxPrice);
    }

    private BooleanExpression tagsOr(final List<WeddingHallTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        final List<String> names = tags.stream().map(Enum::name).toList();
        return tag.name.in(names);
    }

    private OrderSpecifier<?>[] toOrder(final SortType sort) {
        final SortType s = (sort == null) ? SortType.LATEST : sort;
        return switch (s) {
            case PRICE_ASC ->
                new OrderSpecifier<?>[]{weddingHall.price.asc(), weddingHall.id.desc()};
            case PRICE_DESC ->
                new OrderSpecifier<?>[]{weddingHall.price.desc(), weddingHall.id.desc()};
            case POPULAR -> new OrderSpecifier<?>[]{weddingHall.starCount.desc(),
                weddingHall.averageRating.desc(), weddingHall.id.desc()};
            case LATEST -> DEFAULT_LATEST_ORDER;
        };
    }

    private <T> JPAQuery<T> paginate(final JPAQuery<T> q, final Pageable pageable) {
        return q.offset(pageable.getOffset()).limit(pageable.getPageSize());
    }

    private <T> JPAQuery<T> paginate(final JPAQuery<T> q, final int pageSize,
        final int pageNumber) {
        return q.offset((long) (pageNumber - 1) * pageSize).limit(pageSize);
    }

    private Map<Long, List<TagResponse>> loadTagsGroupedByProductId(final List<Long> productIds) {
        final List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(productIds))
            .fetch();

        return allTags.stream()
            .collect(Collectors.groupingBy(
                t -> t.getProduct().getId(),
                Collectors.mapping(t -> new TagResponse(t.getId(), t.getName()),
                    Collectors.toList())
            ));
    }
}
