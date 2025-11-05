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
import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse.TagResponse;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.WeddingHallTag;
import gnu.project.backend.product.repository.WeddingHallCustomRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    // 최신순 (updatedAt desc → id desc)
    private static final OrderSpecifier<?>[] HALL_DEFAULT_ORDER = {
        weddingHall.updatedAt.desc(),
        weddingHall.id.desc()
    };
    private final JPAQueryFactory query;

    /**
     * 상세 조회: MultipleBagFetchException 회피를 위해 이미지만 fetch join
     */
    @Override
    public WeddingHallResponse findByWeddingHallId(final Long id) {
        final WeddingHall hall = query
            .selectFrom(weddingHall)
            .leftJoin(weddingHall.images, image).fetchJoin()
            .where(
                weddingHall.id.eq(id),
                weddingHall.isDeleted.isFalse()
            )
            .fetchOne();

        return WeddingHallResponse.from(Objects.requireNonNull(hall));
    }


    /**
     * 오너 전용 목록
     */
    @Override
    public Page<WeddingHallPageResponse> searchWeddingHallByOwner(
        final String ownerSocialId,
        final Pageable pageable
    ) {
        final List<WeddingHallPageResponse> halls = pagination(
            query
                .select(createPageProjection())
                .from(weddingHall)
                .leftJoin(weddingHall.images, image)
                .where(
                    weddingHall.isDeleted.isFalse(),
                    weddingHall.owner.oauthInfo.socialId.eq(ownerSocialId),
                    image.displayOrder.eq(0).or(image.isNull())
                )
                .orderBy(HALL_DEFAULT_ORDER),
            pageable
        ).fetch();

        if (halls.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        final Map<Long, List<TagResponse>> tagsMap = loadTagsGroupedByProductId(
            halls.stream().map(WeddingHallPageResponse::id).toList()
        );

        final List<WeddingHallPageResponse> withTags = halls.stream()
            .map(h -> new WeddingHallPageResponse(
                h.id(),
                h.name(),
                h.starCount(),
                h.address(),
                h.detail(),
                h.price(),
                h.availableTime(),
                h.createdAt(),
                h.thumbnail(),
                h.region(),
                tagsMap.getOrDefault(h.id(), List.of())
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
        return cnt != null ? cnt : 0L;
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
        return cnt != null ? cnt : 0L;
    }

    private ConstructorExpression<WeddingHallPageResponse> createPageProjection() {
        return Projections.constructor(
            WeddingHallPageResponse.class,
            weddingHall.id,                               // Long id
            weddingHall.name,                             // String name
            weddingHall.starCount,                        // Double starCount
            weddingHall.address,                          // String address
            weddingHall.detail,                           // String detail
            weddingHall.price,                            // Integer price
            weddingHall.availableTimes,                   // String availableTime
            weddingHall.createdAt,                        // LocalDateTime createdAt
            image.url,                                    // String thumbnail (displayOrder=0)
            weddingHall.region,                           // Region region
            weddingHall.owner.bzName.coalesce(weddingHall.owner.oauthInfo.name),
            Expressions.nullExpression(List.class)
        );
    }

    private <T> JPAQuery<T> pagination(final JPAQuery<T> q, final Pageable pageable) {
        return q.offset(pageable.getOffset()).limit(pageable.getPageSize());
    }

    private Map<Long, List<TagResponse>> loadTagsGroupedByProductId(final List<Long> productIds) {
        final List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(productIds))
            .fetch();

        return allTags.stream()
            .collect(Collectors.groupingBy(
                t -> t.getProduct().getId(),
                Collectors.mapping(TagResponse::from, Collectors.toList())
            ));
    }

    @Override
    public List<WeddingHallPageResponse> searchWeddingHallByFilter(
        List<WeddingHallTag> tags,
        Category category,
        Region region,
        Integer minPrice,
        Integer maxPrice,
        SortType sortType,
        Integer pageNumber,
        Integer pageSize
    ) {
        OrderSpecifier<?> order = switch (sortType) {
            case PRICE_ASC -> weddingHall.price.asc();
            case PRICE_DESC -> weddingHall.price.desc();
            case LATEST -> weddingHall.createdAt.desc();
            default -> weddingHall.createdAt.desc();
        };

        List<WeddingHallPageResponse> halls = pagination(
            query
                .select(createWeddingHallResponse())
                .from(weddingHall)
                .leftJoin(weddingHall.images, image)
                .where(image.displayOrder.eq(0).or(image.isNull()))
                .leftJoin(weddingHall.tags, tag)
                .where(
                    regionEq(region),
                    priceBetween(minPrice, maxPrice),
                    tagsIn(tags),
                    weddingHall.isDeleted.eq(false)
                )
                .orderBy(order),
            pageSize,
            pageNumber
        ).fetch();

        if (halls.isEmpty()) {
            return List.of();
        }

        // ✅ 태그 매핑
        List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(halls.stream()
                .map(WeddingHallPageResponse::id)
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

        return halls.stream()
            .map(hall -> new WeddingHallPageResponse(
                hall.id(),
                hall.name(),
                hall.starCount(),
                hall.address(),
                hall.detail(),
                hall.price(),
                hall.availableTime(),
                hall.createdAt(),
                hall.thumbnail(),
                hall.region(),
                tagsMap.getOrDefault(hall.id(), List.of())
            ))
            .toList();
    }

    @Override
    public Long countWeddingHallByFilter(
        List<WeddingHallTag> tags,
        Category category,
        Region region,
        Integer minPrice,
        Integer maxPrice
    ) {
        return query
            .select(weddingHall.countDistinct())
            .from(weddingHall)
            .leftJoin(weddingHall.tags, tag)
            .where(
                regionEq(region),
                priceBetween(minPrice, maxPrice),
                tagsIn(tags),
                weddingHall.isDeleted.eq(false)
            )
            .fetchOne();
    }

    private BooleanExpression regionEq(Region region) {
        return region != null ? weddingHall.region.eq(region) : null;
    }

    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
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

    private BooleanExpression tagsIn(List<WeddingHallTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tag.name.in(tags.stream().map(WeddingHallTag::name).toList());
    }

    private ConstructorExpression<WeddingHallPageResponse> createWeddingHallResponse() {
        return Projections.constructor(
            WeddingHallPageResponse.class,
            weddingHall.id,
            weddingHall.name,
            weddingHall.starCount,
            weddingHall.address,
            weddingHall.detail,
            weddingHall.price,
            weddingHall.availableTimes,
            weddingHall.createdAt,
            image.url,
            weddingHall.region,
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
