package gnu.project.backend.chat.repository;

import gnu.project.backend.chat.dto.response.ChatRoomListResponse;

import java.util.List;

public interface ChatRoomQueryRepository {
    List<ChatRoomListResponse> findRoomsByOwner(String ownerId);
    List<ChatRoomListResponse> findRoomsByCustomer(String customerId);
}
