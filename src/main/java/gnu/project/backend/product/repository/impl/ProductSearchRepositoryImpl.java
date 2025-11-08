package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.product.entity.QProduct.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.repository.ProductSearchCustomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepositoryImpl implements ProductSearchCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public List<ProductPageResponse> searchAll(
        final String keyword,
        final SortType sortType,
        final int pageSize,
        final int pageNumber
    ) {
        return query.selectFrom(product)
            .where(
                keyword != null ? product.name.containsIgnoreCase(keyword)
                    .or(product.detail.containsIgnoreCase(keyword)) : null
            )
            .orderBy(sortType(sortType))
            .offset((long) (pageNumber - 1) * pageSize)
            .limit(pageSize)
            .fetch();
    }
}
