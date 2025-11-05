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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeddingHallRepositoryImpl implements WeddingHallCustomRepository {

    private final JPAQueryFactory query;
    @PersistenceContext
    private EntityManager em;

    @Override
    public WeddingHallResponse findByWeddingHallId(final Long id) {
        final TypedQuery<WeddingHall> query = em.createQuery(
            """
                SELECT DISTINCT w
                FROM WeddingHall w
                LEFT JOIN FETCH w.owner o
                LEFT JOIN FETCH w.images imgs
                WHERE w.id = :id
                  AND w.isDeleted = false
                """,
            WeddingHall.class
        );
        query.setParameter("id", id);

        final List<WeddingHall> result = query.getResultList();
        if (result.isEmpty()) {
            return null; // 서비스에서 null 체크 후 BusinessException 던질 예정
        }

        final WeddingHall hall = result.get(0);
        return WeddingHallResponse.from(hall);
    }


    @Override
    public List<WeddingHallPageResponse> searchWeddingHall(
        final int pageSize,
        final int pageNumber,
        final Region region
    ) {
        final int offset = (pageNumber - 1) * pageSize;

        String jpql = """
                SELECT DISTINCT w
                FROM WeddingHall w
                LEFT JOIN FETCH w.owner o
                LEFT JOIN FETCH w.tags t
                WHERE w.isDeleted = false
            """;

        if (region != null) {
            jpql += " AND w.region = :region ";
        }

        jpql += " ORDER BY w.id DESC ";

        final TypedQuery<WeddingHall> query = em.createQuery(jpql, WeddingHall.class);

        if (region != null) {
            query.setParameter("region", region);
        }

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);

        final List<WeddingHall> halls = query.getResultList();

        return halls.stream()
            .map(hall ->
                new WeddingHallPageResponse(
                    hall.getId(),
                    hall.getName(),
                    hall.getStarCount(),
                    hall.getAddress(),
                    hall.getDetail(),
                    hall.getPrice(),
                    hall.getHallType(),
                    hall.getAvailableTimes(),
                    hall.getCreatedAt(),
                    hall.getThumbnailUrl(),
                    hall.getRegion(),
                    hall.getTags().stream()
                        .map(tag -> new WeddingHallResponse.TagResponse(
                            tag.getId(),
                            tag.getName()
                        ))
                        .toList()
                )
            )
            .toList();
    }

    @Override
    public long countActiveByRegion(final Region region) {
        String jpql = """
                SELECT COUNT(w)
                FROM WeddingHall w
                WHERE w.isDeleted = false
            """;

        if (region != null) {
            jpql += " AND w.region = :region ";
        }

        final TypedQuery<Long> query = em.createQuery(jpql, Long.class);

        if (region != null) {
            query.setParameter("region", region);
        }

        return query.getSingleResult();
    }


    @Override
    public List<WeddingHallPageResponse> searchWeddingHallByOwner(
        final String ownerSocialId,
        final int pageSize,
        final int pageNumber
    ) {
        final int offset = (pageNumber - 1) * pageSize;

        final TypedQuery<WeddingHall> query = em.createQuery(
            """
                SELECT DISTINCT w
                FROM WeddingHall w
                LEFT JOIN FETCH w.owner o
                LEFT JOIN FETCH w.tags t
                WHERE w.isDeleted = false
                  AND o.oauthInfo.socialId = :socialId
                ORDER BY w.id DESC
                """,
            WeddingHall.class
        );
        query.setParameter("socialId", ownerSocialId);
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);

        final List<WeddingHall> halls = query.getResultList();

        return halls.stream()
            .map(hall ->
                new WeddingHallPageResponse(
                    hall.getId(),
                    hall.getName(),
                    hall.getStarCount(),
                    hall.getAddress(),
                    hall.getDetail(),
                    hall.getPrice(),
                    hall.getHallType(),
                    hall.getAvailableTimes(),
                    hall.getCreatedAt(),
                    hall.getThumbnailUrl(),
                    hall.getRegion(),
                    hall.getTags().stream()
                        .map(tag -> new WeddingHallResponse.TagResponse(
                            tag.getId(),
                            tag.getName()
                        ))
                        .toList()
                )
            )
            .toList();
    }


    @Override
    public Optional<WeddingHall> findWeddingHallWithImagesAndOptionsById(final Long id) {
        final TypedQuery<WeddingHall> query = em.createQuery(
            """
                SELECT DISTINCT w
                FROM WeddingHall w
                LEFT JOIN FETCH w.owner o
                LEFT JOIN FETCH w.images imgs
                WHERE w.id = :id
                  AND w.isDeleted = false
                """,
            WeddingHall.class
        );

        query.setParameter("id", id);
        final List<WeddingHall> result = query.getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }


    @Override
    public long countActive() {
        final TypedQuery<Long> query = em.createQuery(
            """
                SELECT COUNT(w)
                FROM WeddingHall w
                WHERE w.isDeleted = false
                """,
            Long.class
        );
        return query.getSingleResult();
    }

    @Override
    public long countActiveByOwner(final String ownerSocialId) {
        final TypedQuery<Long> query = em.createQuery(
            """
                SELECT COUNT(w)
                FROM WeddingHall w
                JOIN w.owner o
                WHERE w.isDeleted = false
                  AND o.oauthInfo.socialId = :socialId
                """,
            Long.class
        );
        query.setParameter("socialId", ownerSocialId);
        return query.getSingleResult();
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
                hall.hallType(),
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
            weddingHall.hallType,
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
