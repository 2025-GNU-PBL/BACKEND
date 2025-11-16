package gnu.project.backend.chat.repository;

import gnu.project.backend.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomQueryRepository {
    Optional<ChatRoom> findByOwnerIdAndCustomerId(String ownerId, String customerId);

    List<ChatRoom> findByOwnerDeletedIsTrueAndCustomerDeletedIsTrue();
}