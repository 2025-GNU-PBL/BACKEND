package gnu.project.backend.chat.service;

import gnu.project.backend.chat.constant.ChatConstants;
import gnu.project.backend.chat.dto.request.ChatMessageRequest;
import gnu.project.backend.chat.dto.request.ChatRoomCreateRequest;
import gnu.project.backend.chat.dto.response.ChatMessageResponse;
import gnu.project.backend.chat.dto.response.ChatRoomListResponse;
import gnu.project.backend.chat.entity.ChatRoom;
import gnu.project.backend.chat.entity.Chatting;
import gnu.project.backend.chat.repository.ChatRoomRepository;
import gnu.project.backend.chat.repository.ChattingRepository;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.common.error.ErrorCode;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChattingRepository chattingRepository;

    // 실제 사용자 존재/ROLE 확인용
    private final OwnerRepository ownerRepository;
    private final CustomerRepository customerRepository;

    /**
     * ownerId, customerId 조합으로만 방을 만든다. (역순 매칭은 제거)
     */
    public Long createOrGetRoom(String ownerId, String customerId) {
        return chatRoomRepository.findByOwnerIdAndCustomerId(ownerId, customerId)
            .map(ChatRoom::getId)
            .orElseGet(() -> {
                ChatRoom room = ChatRoom.builder()
                    .ownerId(ownerId)
                    .customerId(customerId)
                    .build();
                chatRoomRepository.save(room);
                return room.getId();
            });
    }

    public ChatMessageResponse saveMessage(ChatMessageRequest req) {
        ChatRoom room = chatRoomRepository.findById(req.chatRoomId())
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));
        //TODO : 대체

        // 1) 우리 시스템에 실제로 있는 유저인지 + ROLE이 맞는지
        verifySender(req.senderRole(), req.senderId());

        // 2) 그 유저가 이 방의 멤버가 맞는지
        validateParticipant(room, req.senderRole(), req.senderId());

        LocalDateTime now = LocalDateTime.now();

        boolean ownerRead = false;
        boolean customerRead = false;
        LocalDateTime ownerReadAt = null;
        LocalDateTime customerReadAt = null;

        if (ChatConstants.ROLE_OWNER.equalsIgnoreCase(req.senderRole())) {
            ownerRead = true;
            ownerReadAt = now;
        } else if (ChatConstants.ROLE_CUSTOMER.equalsIgnoreCase(req.senderRole())) {
            customerRead = true;
            customerReadAt = now;
        }

        Chatting chatting = Chatting.builder()
            .chatRoom(room)
            .message(req.message())
            .senderRole(req.senderRole())
            .senderId(req.senderId())
            .sendTime(now)
            .ownerRead(ownerRead)
            .ownerReadAt(ownerReadAt)
            .customerRead(customerRead)
            .customerReadAt(customerReadAt)
            .build();

        chattingRepository.save(chatting);

        return new ChatMessageResponse(
            room.getId(),
            chatting.getSenderRole(),
            chatting.getSenderId(),
            chatting.getMessage(),
            chatting.getSendTime(),
            chatting.isOwnerRead(),
            chatting.isCustomerRead(),
            chatting.getOwnerReadAt(),
            chatting.getCustomerReadAt(),
            chatting.getId()
        );
    }

    /**
     * 실제 owner/customer 엔티티로 검증
     */
    private void verifySender(String senderRole, String senderSocialId) {
        if (senderRole == null || senderSocialId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (ChatConstants.ROLE_OWNER.equalsIgnoreCase(senderRole)) {
            Owner owner = ownerRepository.findByOauthInfo_SocialId(senderSocialId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OWNER_NOT_FOUND_EXCEPTION));

            if (owner.getUserRole() != UserRole.OWNER) {
                throw new BusinessException(ErrorCode.ROLE_IS_NOT_VALID);
            }
            return;
        }

        if (ChatConstants.ROLE_CUSTOMER.equalsIgnoreCase(senderRole)) {
            Customer customer = customerRepository.findByOauthInfo_SocialId(senderSocialId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION));

            if (!customer.isActive()) {
                throw new BusinessException(ErrorCode.CUSTOMER_DELETED_EXCEPTION);
            }
            if (customer.getUserRole() != UserRole.CUSTOMER) {
                throw new BusinessException(ErrorCode.ROLE_IS_NOT_VALID);
            }
            return;
        }

        throw new BusinessException(ErrorCode.ROLE_IS_NOT_VALID);
    }

    /**
     * 방에 저장돼 있는 owner_id / customer_id 와도 일치하는지 확인
     */
    private void validateParticipant(ChatRoom room, String senderRole, String senderId) {
        if (ChatConstants.ROLE_OWNER.equalsIgnoreCase(senderRole)) {
            if (!senderId.equals(room.getOwnerId())) {
                throw new BusinessException(ErrorCode.AUTH_FORBIDDEN);
            }
            return;
        }

        if (ChatConstants.ROLE_CUSTOMER.equalsIgnoreCase(senderRole)) {
            if (!senderId.equals(room.getCustomerId())) {
                throw new BusinessException(ErrorCode.AUTH_FORBIDDEN);
            }
            return;
        }

        throw new BusinessException(ErrorCode.ROLE_IS_NOT_VALID);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResponse> getRoomsByOwner(String ownerId) {
        return chatRoomRepository.findRoomsByOwner(ownerId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResponse> getRoomsByCustomer(String customerId) {
        return chatRoomRepository.findRoomsByCustomer(customerId);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(Long chatRoomId, Long cursor, int size) {
        return chattingRepository.findPage(chatRoomId, cursor, size)
            .stream()
            .map(c -> new ChatMessageResponse(
                chatRoomId,
                c.getSenderRole(),
                c.getSenderId(),
                c.getMessage(),
                c.getSendTime(),
                c.isOwnerRead(),
                c.isCustomerRead(),
                c.getOwnerReadAt(),
                c.getCustomerReadAt(),
                c.getId()
            ))
            .toList();
    }

    public void readAll(Long chatRoomId, String role) {
        if (role == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        LocalDateTime now = LocalDateTime.now();

        if (ChatConstants.ROLE_OWNER.equalsIgnoreCase(role)) {
            chattingRepository.bulkReadByOwner(chatRoomId, now);
            return;
        }
        if (ChatConstants.ROLE_CUSTOMER.equalsIgnoreCase(role)) {
            chattingRepository.bulkReadByCustomer(chatRoomId, now);
            return;
        }

        throw new BusinessException(ErrorCode.ROLE_IS_NOT_VALID);
    }

    public Long createRoomByDto(ChatRoomCreateRequest request) {
        return createOrGetRoom(request.ownerId(), request.customerId());
    }

    public void deleteRoomForSide(Long chatRoomId, String role, String senderId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));

        // 내가 그 방 사람 맞는지도 한 번 더
        validateParticipant(room, role, senderId);

        if (ChatConstants.ROLE_OWNER.equalsIgnoreCase(role)) {
            room.deleteByOwner();
            return;
        }
        if (ChatConstants.ROLE_CUSTOMER.equalsIgnoreCase(role)) {
            room.deleteByCustomer();
            return;
        }
        throw new BusinessException(ErrorCode.ROLE_IS_NOT_VALID);
    }
}
