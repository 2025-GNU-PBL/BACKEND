package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.owner.entity.QOwner.owner;
import static gnu.project.backend.product.entity.QImage.image;
import static gnu.project.backend.product.entity.QProduct.product;
import static gnu.project.backend.product.entity.QTag.tag;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.dto.response.TagResponse;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.entity.Tag;
import gnu.project.backend.product.repository.ProductCustomRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

    private static final OrderSpecifier<?>[] DEFAULT_LATEST_ORDER = {
        product.createdAt.desc(),
        product.id.desc()
    };
    private final JPAQueryFactory query;

    @Override
    public Optional<Product> findByIdWithOwner(final Long id) {
        return Optional.ofNullable(
            query.selectFrom(product)
                .leftJoin(product.owner, owner)
                .fetchJoin()
                .where(product.id.eq(id).and(product.isDeleted.isFalse()))
                .fetchFirst()
        );
    }

    @Override
    public Page<ProductPageResponse> findProductsById(
        final Long id,
        final int pageNumber,
        final int pageSize
    ) {
        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        BooleanBuilder imageCondition = new BooleanBuilder();
        imageCondition.and(image.displayOrder.eq(0));

        List<ProductPageResponse> response = query
            .selectDistinct(createProductResponse())
            .from(product)
            .leftJoin(product.images, image).on(imageCondition)
            .where(product.owner.id.eq(id).and(product.isDeleted.isFalse()))
            .orderBy(DEFAULT_LATEST_ORDER)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        if (!response.isEmpty()) {
            List<Long> productIds = response.stream()
                .map(ProductPageResponse::id)
                .toList();

            List<Tag> allTags = query
                .selectFrom(tag)
                .where(tag.product.id.in(productIds).and(tag.product.isDeleted.isFalse()))
                .fetch();

            Map<Long, List<TagResponse>> tagsMap = allTags.stream()
                .collect(Collectors.groupingBy(
                    t -> t.getProduct().getId(),
                    Collectors.mapping(
                        t -> new TagResponse(t.getId(), t.getName()),
                        Collectors.toList()
                    )
                ));

            response = response.stream()
                .map(p -> new ProductPageResponse(
                    p.id(),
                    p.name(),
                    p.starCount(),
                    p.address(),
                    p.detail(),
                    p.price(),
                    p.availableTime(),
                    p.createdAt(),
                    p.region(),
                    p.thumbnail(),
                    p.category(),
                    p.bzName(),
                    tagsMap.getOrDefault(p.id(), List.of())
                ))
                .toList();
        }

        Long total = query
            .select(product.count())
            .from(product)
            .where(product.owner.id.eq(id).and(product.isDeleted.isFalse()))
            .fetchOne();

        if (total == null) {
            total = 0L;
        }

        return new PageImpl<>(response, pageable, total);
    }

    private ConstructorExpression<ProductPageResponse> createProductResponse() {
        return Projections.constructor(
            ProductPageResponse.class,
            product.id,
            product.name,
            product.starCount,
            product.address,
            product.detail,
            product.price,
            product.availableTimes,
            product.createdAt,
            product.region,
            image.url,
            product.category,
            product.owner.bzName,
            Expressions.nullExpression(List.class)
        );
    }
}
