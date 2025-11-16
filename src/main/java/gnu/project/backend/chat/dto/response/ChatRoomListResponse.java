package gnu.project.backend.chat.dto.response;

import gnu.project.backend.product.enumerated.Category;

import java.time.LocalDateTime;

public record ChatRoomListResponse(
        Long chatRoomId,
        String opponentId,
        String opponentName,
        String opponentProfileImage,
        String lastMessage,
        LocalDateTime lastMessageTime,
        long unreadCount,
        Category lastProductCategory
) {}
