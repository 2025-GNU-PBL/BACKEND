package gnu.project.backend.chat.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.chat.entity.Chatting;
import gnu.project.backend.chat.entity.QChatting;
import gnu.project.backend.chat.repository.ChattingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChattingQueryRepositoryImpl implements ChattingQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public int bulkReadByOwner(Long chatRoomId, LocalDateTime now) {
        QChatting ct = QChatting.chatting;
        return (int) query
                .update(ct)
                .set(ct.ownerRead, true)
                .set(ct.ownerReadAt, now)
                .where(
                        ct.chatRoom.id.eq(chatRoomId),
                        ct.ownerRead.isFalse()
                )
                .execute();
    }

    @Override
    public int bulkReadByCustomer(Long chatRoomId, LocalDateTime now) {
        QChatting ct = QChatting.chatting;
        return (int) query
                .update(ct)
                .set(ct.customerRead, true)
                .set(ct.customerReadAt, now)
                .where(
                        ct.chatRoom.id.eq(chatRoomId),
                        ct.customerRead.isFalse()
                )
                .execute();
    }

    @Override
    public List<Chatting> findPage(Long chatRoomId, Long lastId, int size) {
        QChatting ct = QChatting.chatting;

        return query
                .selectFrom(ct)
                .where(
                        ct.chatRoom.id.eq(chatRoomId),
                        lastId != null ? ct.id.lt(lastId) : null
                )
                .orderBy(ct.id.desc())
                .limit(size)
                .fetch();
    }
}
