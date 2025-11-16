package gnu.project.backend.chat.repository;

import gnu.project.backend.chat.dto.response.ChatRoomListResponse;
import gnu.project.backend.product.enumerated.Category;

import java.util.List;

public interface ChatRoomQueryRepository {
    List<ChatRoomListResponse> findRoomsByOwner(String ownerId, Category category);
    List<ChatRoomListResponse> findRoomsByCustomer(String customerId, Category category);
}
