package gnu.project.backend.chat.repository;

import gnu.project.backend.chat.entity.Chatting;

import java.time.LocalDateTime;
import java.util.List;

public interface ChattingQueryRepository {
    int bulkReadByOwner(Long chatRoomId, LocalDateTime now);
    int bulkReadByCustomer(Long chatRoomId, LocalDateTime now);

    // 페이징
    List<Chatting> findPage(Long chatRoomId, Long lastId, int size);
}
