package gnu.project.backend.chat.repository;

import gnu.project.backend.chat.entity.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Long>, ChattingQueryRepository {
    List<Chatting> findByChatRoomIdOrderBySendTimeAsc(Long chatRoomId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Chatting c where c.chatRoom.id in :roomIds")
    void deleteByChatRoomIds(Collection<Long> roomIds);
}
