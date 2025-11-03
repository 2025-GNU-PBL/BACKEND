package gnu.project.backend.chat.scheduler;

import gnu.project.backend.chat.entity.ChatRoom;
import gnu.project.backend.chat.repository.ChatRoomRepository;
import gnu.project.backend.chat.repository.ChattingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatCleanupScheduler {

    private final ChatRoomRepository chatRoomRepository;
    private final ChattingRepository chattingRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void purgeFullyDeletedRooms() {
        List<ChatRoom> rooms = chatRoomRepository.findByOwnerDeletedIsTrueAndCustomerDeletedIsTrue();
        if (rooms.isEmpty()) {
            return;
        }

        var roomIds = rooms.stream().map(ChatRoom::getId).toList();
        log.info("[ChatCleanup] target rooms: {}", roomIds);

        // 1) 메시지 먼저 삭제
        chattingRepository.deleteByChatRoomIds(roomIds);
        // 2) 방 삭제
        chatRoomRepository.deleteAllById(roomIds);

        log.info("[ChatCleanup] deleted {} rooms", roomIds.size());
    }
}
