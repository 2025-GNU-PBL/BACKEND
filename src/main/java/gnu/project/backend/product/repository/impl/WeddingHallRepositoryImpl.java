package gnu.project.backend.product.repository.impl;

import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enurmerated.Region;
import gnu.project.backend.product.repository.WeddingHallCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeddingHallRepositoryImpl implements WeddingHallCustomRepository {

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
}
