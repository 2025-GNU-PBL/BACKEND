package gnu.project.backend.product.repository.impl;

import static gnu.project.backend.owner.entity.QOwner.owner;
import static gnu.project.backend.product.entity.QProduct.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ProductCustomRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory query;


    @Override
    public Optional<Product> findByIdWithOwner(final Long id) {
        return Optional.ofNullable(
            query.selectFrom(product)
                .leftJoin(product.owner, owner)
                .fetchJoin()
                .where(product.id.eq(id))
                .fetchFirst()
        );
    }
}
