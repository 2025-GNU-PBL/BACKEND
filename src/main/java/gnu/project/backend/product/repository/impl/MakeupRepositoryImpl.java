package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QMakeup.makeup;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.MakeupPageResponse;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.repository.MakeupCustomRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
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
    public List<MakeupPageResponse> searchMakeup(final int pageSize, final int pageNumber) {
        return pagination(query
                .select(createLessonResponse())
                .from(makeup)
                .leftJoin(makeup.images, image)
                .where(image.displayOrder.eq(0).or(image.isNull()))
                .orderBy(MAKEUP_DEFAULT_ORDER),
            pageSize,
            pageNumber
        ).fetch();
    }

    private ConstructorExpression<MakeupPageResponse> createLessonResponse() {
        return Projections.constructor(
            MakeupPageResponse.class,
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