package gnu.project.backend.review.repository;

import gnu.project.backend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository
        extends JpaRepository<Review, Long>, ReviewCustomRepository {
}