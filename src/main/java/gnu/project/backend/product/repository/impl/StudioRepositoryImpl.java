package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QOption.option;
import static gnu.project.backend.product.entity.QStudio.studio;
import static gnu.project.backend.product.entity.QTag.tag;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.DressResponse.TagResponse;
import gnu.project.backend.product.dto.response.StudioPageResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import gnu.project.backend.product.entity.Studio;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.repository.StudioCustomRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudioRepositoryImpl implements StudioCustomRepository {

    private static final OrderSpecifier<?>[] STUDIO_DEFAULT_ORDER = {
        studio.id.desc(),
        studio.createdAt.desc()
    };
    private final JPAQueryFactory query;

    @Override
    public StudioResponse findByStudioId(final Long id) {
        Studio result = query
            .selectFrom(studio)
            .distinct()
            .leftJoin(studio.images, image).fetchJoin()
            .leftJoin(studio.tags, tag)
            .where(studio.id.eq(id))
            .fetchOne();
        return StudioResponse.from(Objects.requireNonNull(result));
    }

    @Override
    public List<StudioPageResponse> searchStudio(int pageSize, int pageNumber) {
        List<StudioPageResponse> studios = pagination(
            query
                .select(createDressResponse())
                .from(studio)
                .leftJoin(studio.images, image)
                .where(image.displayOrder.eq(0).or(image.isNull()))
                .orderBy(STUDIO_DEFAULT_ORDER),
            pageSize,
            pageNumber
        ).fetch();

        if (studios.isEmpty()) {
            return List.of();
        }

        List<Tag> allTags = query
            .selectFrom(tag)
            .where(tag.product.id.in(studios.stream()
                .map(StudioPageResponse::id)
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

        return studios.stream()
            .map(studio -> new StudioPageResponse(
                studio.id(),
                studio.name(),
                studio.starCount(),
                studio.address(),
                studio.detail(),
                studio.price(),
                studio.availableTime(),
                studio.createdAt(),
                studio.Thumbnail(),
                tagsMap.getOrDefault(studio.id(), List.of())
            ))
            .toList();
    }

    @Override
    public Optional<Studio> findStudioWithImagesAndOptionsById(Long id) {
        return Optional.ofNullable(
            query
                .selectFrom(studio)
                .leftJoin(studio.images, image).fetchJoin()
                .leftJoin(studio.options, option)
                .leftJoin(studio.tags, tag)
                .where(studio.id.eq(id))
                .fetchOne()
        );
    }

    private ConstructorExpression<StudioPageResponse> createDressResponse() {
        return Projections.constructor(
            StudioPageResponse.class,
            studio.id,
            studio.name,
            studio.starCount,
            studio.address,
            studio.detail,
            studio.price,
            studio.availableTimes,
            studio.createdAt,
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
