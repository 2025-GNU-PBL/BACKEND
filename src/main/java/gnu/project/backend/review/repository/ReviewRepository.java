package gnu.project.backend.review.repository;

import gnu.project.backend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// ✅ 1. <Review, Long> 제네릭 타입 추가
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // ✅ 2. 올바른 쿼리 메서드 이름과 파라미터 타입으로 수정
    List<Review> findByProduct_Id(Long productId);

    List<Review> findByCustomer_Id(Long customerId);

}