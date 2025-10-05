package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QDress.dress;
import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QTag.tag;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.DressPageResponse;
import gnu.project.backend.product.dto.response.DressResponse;
import gnu.project.backend.product.dto.response.DressResponse.TagResponse;
import gnu.project.backend.product.entity.Dress;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.repository.DressCustomRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        // 4단계: 태그가 포함된 새로운 DressPageResponse 생성
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
                tagsMap.getOrDefault(dress.id(), List.of())
            ))
            .toList();
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
