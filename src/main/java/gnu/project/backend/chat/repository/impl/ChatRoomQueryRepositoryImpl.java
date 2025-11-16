package gnu.project.backend.chat.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.chat.constant.ChatConstants;
import gnu.project.backend.chat.dto.response.ChatRoomListResponse;
import gnu.project.backend.chat.entity.QChatRoom;
import gnu.project.backend.chat.entity.QChatting;
import gnu.project.backend.chat.repository.ChatRoomQueryRepository;
import gnu.project.backend.customer.entity.QCustomer;
import gnu.project.backend.owner.entity.QOwner;
import gnu.project.backend.product.enumerated.Category;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public List<ChatRoomListResponse> findRoomsByOwner(String ownerId, Category category) {
        QChatRoom cr = QChatRoom.chatRoom;
        QChatting ct = QChatting.chatting;
        QCustomer cu = QCustomer.customer;

        return query
                .select(Projections.constructor(
                        ChatRoomListResponse.class,
                        cr.id,                               // chatRoomId
                        cr.customerId,                       // opponentId
                        cu.oauthInfo.name,                   // opponentName (고객은 실명 정책 유지)
                        Expressions.nullExpression(String.class), // opponentProfileImage
                        ct.message.max(),                    // lastMessage
                        ct.sendTime.max(),                   // lastMessageTime
                        new CaseBuilder()
                                .when(ct.senderRole.ne(ChatConstants.ROLE_OWNER).and(ct.ownerRead.isFalse()))
                                .then(1L)
                                .otherwise(0L)
                                .sum(),
                        cr.lastProductCategory
                ))
                .from(cr)
                .leftJoin(ct).on(ct.chatRoom.eq(cr))
                .leftJoin(cu).on(cu.oauthInfo.socialId.eq(cr.customerId))
                .where(
                        cr.ownerId.eq(ownerId),
                        cr.ownerDeleted.isFalse(),
                        category != null ? cr.lastProductCategory.eq(category) : null
                )
                .groupBy(cr.id, cr.customerId, cu.oauthInfo.name, cr.lastProductCategory)
                .orderBy(ct.sendTime.max().desc().nullsLast())
                .fetch();
    }

    @Override
    public List<ChatRoomListResponse> findRoomsByCustomer(String customerId, Category category) {
        QChatRoom cr = QChatRoom.chatRoom;
        QChatting ct = QChatting.chatting;
        QOwner ow = QOwner.owner;

        // 업체명 우선, 없으면 실명으로 폴백
        StringExpression ownerDisplayName = ow.bzName.coalesce(ow.oauthInfo.name);

        return query
                .select(Projections.constructor(
                        ChatRoomListResponse.class,
                        cr.id,                               // chatRoomId
                        cr.ownerId,                          // opponentId
                        ownerDisplayName,                    // opponentName = bzName 우선
                        ow.profileImage,                     // opponentProfileImage
                        ct.message.max(),                    // lastMessage
                        ct.sendTime.max(),                   // lastMessageTime
                        new CaseBuilder()
                                .when(ct.senderRole.ne(ChatConstants.ROLE_CUSTOMER).and(ct.customerRead.isFalse()))
                                .then(1L)
                                .otherwise(0L)
                                .sum(),
                        cr.lastProductCategory
                ))
                .from(cr)
                .leftJoin(ct).on(ct.chatRoom.eq(cr))
                .leftJoin(ow).on(ow.oauthInfo.socialId.eq(cr.ownerId))
                .where(
                        cr.customerId.eq(customerId),
                        cr.customerDeleted.isFalse(),
                        category != null ? cr.lastProductCategory.eq(category) : null
                )
                // groupBy는 select의 비집계 컬럼/표현식과 동일해야 함
                .groupBy(cr.id, cr.ownerId, ownerDisplayName, ow.profileImage, cr.lastProductCategory)
                .orderBy(ct.sendTime.max().desc().nullsLast())
                .fetch();
    }
}
