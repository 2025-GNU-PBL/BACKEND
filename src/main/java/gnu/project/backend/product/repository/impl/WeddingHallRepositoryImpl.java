package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QTag.tag;
import static gnu.project.backend.product.entity.QWeddingHall.weddingHall;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse.TagResponse;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Region;
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

@RequiredArgsConstructor
public class WeddingHallRepositoryImpl implements WeddingHallCustomRepository {

    // 최신순 (updatedAt desc → id desc)
    private static final OrderSpecifier<?>[] HALL_DEFAULT_ORDER = {
            weddingHall.updatedAt.desc(),
            weddingHall.id.desc()
    };

    private final JPAQueryFactory query;

    /** 상세 조회: MultipleBagFetchException 회피를 위해 이미지만 fetch join */
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

    /** 공개 목록 + 필터(지역/지하철/식사) + 최신순 */
    @Override
    public Page<WeddingHallPageResponse> searchWeddingHall(
            final Pageable pageable,
            final Region region,
            final Boolean subwayAccessible,
            final Boolean diningAvailable
    ) {
        final List<WeddingHallPageResponse> halls = pagination(
                query
                        .select(createPageProjection())
                        .from(weddingHall)
                        .leftJoin(weddingHall.images, image)
                        .where(
                                weddingHall.isDeleted.isFalse(),
                                region != null ? weddingHall.region.eq(region) : null,
                                subwayAccessible != null
                                        ? (subwayAccessible ? weddingHall.subwayAccessible.isTrue()
                                        : weddingHall.subwayAccessible.isFalse())
                                        : null,
                                diningAvailable != null
                                        ? (diningAvailable ? weddingHall.diningAvailable.isTrue()
                                        : weddingHall.diningAvailable.isFalse())
                                        : null,
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
                        h.ownerName(),
                        h.subwayAccessible(),
                        h.diningAvailable(),
                        tagsMap.getOrDefault(h.id(), List.of())
                ))
                .toList();

        final long total = countActiveFiltered(region, subwayAccessible, diningAvailable);
        return new PageImpl<>(withTags, pageable, total);
    }

    /** 총합(필터 적용) */
    @Override
    public long countActiveFiltered(
            final Region region,
            final Boolean subwayAccessible,
            final Boolean diningAvailable
    ) {
        final Long cnt = query
                .select(weddingHall.count())
                .from(weddingHall)
                .where(
                        weddingHall.isDeleted.isFalse(),
                        region != null ? weddingHall.region.eq(region) : null,
                        subwayAccessible != null
                                ? (subwayAccessible ? weddingHall.subwayAccessible.isTrue()
                                : weddingHall.subwayAccessible.isFalse())
                                : null,
                        diningAvailable != null
                                ? (diningAvailable ? weddingHall.diningAvailable.isTrue()
                                : weddingHall.diningAvailable.isFalse())
                                : null
                )
                .fetchOne();
        return cnt != null ? cnt : 0L;
    }

    /** 오너 전용 목록 */
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
                        h.ownerName(),
                        h.subwayAccessible(),
                        h.diningAvailable(),
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
                weddingHall.subwayAccessible,
                weddingHall.diningAvailable,
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
}
