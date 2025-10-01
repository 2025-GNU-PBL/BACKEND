package gnu.project.backend.review.repository;

import gnu.project.backend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ReviewRepository extends JpaRepository<Review, Long> {


    List<Review> findByProduct_Id(Long productId);

    List<Review> findByCustomer_Id(Long customerId);

}