package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QDress.dress;
import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QMakeup.makeup;
import static gnu.project.backend.product.entity.QTag.tag;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.DressPageResponse;
import gnu.project.backend.product.dto.response.DressResponse;
import gnu.project.backend.product.entity.Dress;
import gnu.project.backend.product.repository.DressCustomRepository;
import java.util.List;
import java.util.Objects;
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
            .leftJoin(dress.tags, tag).fetchJoin()
            .where(dress.id.eq(id))
            .fetchOne();
        return DressResponse.from(Objects.requireNonNull(result));
    }

    @Override
    public List<DressPageResponse> searchDress(int pageSize, int pageNumber) {
        return pagination(query
                .select(createDressResponse())
                .from(dress)
                .leftJoin(dress.images, image)
                .leftJoin(dress.tags, tag)
                .where(image.displayOrder.eq(0).or(image.isNull()))
                .orderBy(DRESS_DEFAULT_ORDER),
            pageSize,
            pageNumber
        ).fetch();
    }

    private ConstructorExpression<DressPageResponse> createDressResponse() {
        return Projections.constructor(
            DressPageResponse.class,
            makeup.id,
            makeup.name,
            makeup.style,
            makeup.starCount,
            makeup.address,
            makeup.detail,
            makeup.price,
            makeup.type,
            makeup.availableTimes,
            makeup.createdAt,
            image.url
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
