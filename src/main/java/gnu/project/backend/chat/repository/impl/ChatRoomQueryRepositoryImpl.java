package gnu.project.backend.chat.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.chat.constant.ChatConstants;
import gnu.project.backend.chat.dto.response.ChatRoomListResponse;
import gnu.project.backend.chat.entity.QChatRoom;
import gnu.project.backend.chat.entity.QChatting;
import gnu.project.backend.chat.repository.ChatRoomQueryRepository;
import gnu.project.backend.customer.entity.QCustomer;
import gnu.project.backend.owner.entity.QOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public List<ChatRoomListResponse> findRoomsByOwner(String ownerId) {
        QChatRoom cr = QChatRoom.chatRoom;
        QChatting ct = QChatting.chatting;
        QCustomer cu = QCustomer.customer;

        return query
                .select(Projections.constructor(
                        ChatRoomListResponse.class,
                        cr.id,
                        cr.customerId,
                        cu.oauthInfo.name,
                        Expressions.nullExpression(String.class),
                        ct.message.max(),
                        ct.sendTime.max(),
                        new CaseBuilder()
                                .when(ct.senderRole.ne(ChatConstants.ROLE_OWNER)
                                        .and(ct.ownerRead.isFalse()))
                                .then(1L)
                                .otherwise(0L)
                                .sum()
                ))
                .from(cr)
                .leftJoin(ct).on(ct.chatRoom.eq(cr))
                .leftJoin(cu).on(cu.oauthInfo.socialId.eq(cr.customerId))
                .where(cr.ownerId.eq(ownerId), cr.ownerDeleted.isFalse())
                .groupBy(cr.id, cr.customerId, cu.oauthInfo.name)
                .orderBy(ct.sendTime.max().desc().nullsLast())
                .fetch();
    }

    @Override
    public List<ChatRoomListResponse> findRoomsByCustomer(String customerId) {
        QChatRoom cr = QChatRoom.chatRoom;
        QChatting ct = QChatting.chatting;
        QOwner ow = QOwner.owner;

        return query
                .select(Projections.constructor(
                        ChatRoomListResponse.class,
                        cr.id,
                        cr.ownerId,
                        ow.oauthInfo.name,
                        ow.profileImage,
                        ct.message.max(),
                        ct.sendTime.max(),
                        new CaseBuilder()
                                .when(ct.senderRole.ne(ChatConstants.ROLE_CUSTOMER)
                                        .and(ct.customerRead.isFalse()))
                                .then(1L)
                                .otherwise(0L)
                                .sum()
                ))
                .from(cr)
                .leftJoin(ct).on(ct.chatRoom.eq(cr))
                .leftJoin(ow).on(ow.oauthInfo.socialId.eq(cr.ownerId))
                .where(cr.customerId.eq(customerId),cr.customerDeleted.isFalse())
                .groupBy(cr.id, cr.ownerId, ow.oauthInfo.name, ow.profileImage)
                .orderBy(ct.sendTime.max().desc().nullsLast())
                .fetch();
    }
}
