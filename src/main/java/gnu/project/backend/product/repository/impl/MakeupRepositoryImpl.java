package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QMakeup.makeup;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.entity.Makeup;
import gnu.project.backend.product.repository.MakeupCustomRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MakeupRepositoryImpl implements MakeupCustomRepository {

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
}