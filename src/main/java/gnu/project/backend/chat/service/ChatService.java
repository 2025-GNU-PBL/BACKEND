package gnu.project.backend.chat.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.chat.constant.ChatConstants;
import gnu.project.backend.chat.dto.request.ChatMessageRequest;
import gnu.project.backend.chat.dto.request.ChatOpenFromProductRequest;
import gnu.project.backend.chat.dto.request.ChatSendRequest;
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
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.repository.ProductRepository;
import java.time.LocalDate;
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
    private final OwnerRepository ownerRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public Long openRoomFromProduct(Accessor accessor, ChatOpenFromProductRequest request) {
        if (request == null || request.productId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        Customer customer = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        String ownerId = product.getOwner().getOauthInfo().getSocialId();
        Category category = product.getCategory();

        ChatRoom room = chatRoomRepository.findByOwnerIdAndCustomerId(ownerId, customer.getOauthInfo().getSocialId())
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.create(ownerId, customer.getOauthInfo().getSocialId())));
        room.touchCategory(category);
        return room.getId();
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResponse> getMyRoomsAsCustomer(Accessor accessor, Category category) {
        return chatRoomRepository.findRoomsByCustomer(accessor.getSocialId(), category);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResponse> getMyRoomsAsOwner(Accessor accessor, Category category) {
        return chatRoomRepository.findRoomsByOwner(accessor.getSocialId(), category);
    }

    // ★ 보안 강화: Accessor 기반 참여자 검증 추가
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(Accessor accessor, Long chatRoomId, Long cursor, int size) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));
        String sid = accessor.getSocialId();
        if (!sid.equals(room.getOwnerId()) && !sid.equals(room.getCustomerId())) {
            throw new BusinessException(ErrorCode.AUTH_FORBIDDEN);
        }

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

    public void readAll(Accessor accessor, Long chatRoomId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));
        String sid = accessor.getSocialId();
        LocalDateTime now = LocalDateTime.now();
        if (sid.equals(room.getOwnerId())) {
            chattingRepository.bulkReadByOwner(chatRoomId, now);
            return;
        }
        if (sid.equals(room.getCustomerId())) {
            chattingRepository.bulkReadByCustomer(chatRoomId, now);
            return;
        }
        throw new BusinessException(ErrorCode.AUTH_FORBIDDEN);
    }

    public ChatMessageResponse sendByRest(Accessor accessor, ChatSendRequest request) {
        if (request == null || request.chatRoomId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (request.message() == null || request.message().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (request.message().length() > ChatConstants.MAX_MESSAGE_LENGTH) {
            // 길이 초과는 BAD_REQUEST
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        ChatRoom room = chatRoomRepository.findById(request.chatRoomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));

        String sid = accessor.getSocialId();
        String role;
        if (sid.equals(room.getOwnerId())) {
            role = ChatConstants.ROLE_OWNER;
        } else if (sid.equals(room.getCustomerId())) {
            role = ChatConstants.ROLE_CUSTOMER;
        } else {
            throw new BusinessException(ErrorCode.AUTH_FORBIDDEN);
        }

        // 일일 전송 제한 체크: 초과시 TOO_MANY_REQUESTS
        enforceDailyLimit(room.getId(), sid);

        Chatting chatting = Chatting.create(room, request.message(), role, sid);
        markReadForSelf(chatting); // 자기 메시지는 자기측 읽음 처리
        chattingRepository.save(chatting);

        return toResponse(room, chatting);
    }

    // --- WS 경로 호환: 기존 시그니처 유지 (ChatWsController가 사용) ---
    public ChatMessageResponse saveMessage(ChatMessageRequest req) {
        if (req == null || req.chatRoomId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (req.message() == null || req.message().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (req.message().length() > ChatConstants.MAX_MESSAGE_LENGTH) {
            // 길이 초과는 BAD_REQUEST
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        ChatRoom room = chatRoomRepository.findById(req.chatRoomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));

        // 기존 WS 경로는 senderRole/senderId를 명시적으로 전달 → 강한 검증 유지
        verifySender(req.senderRole(), req.senderId());
        validateParticipant(room, req.senderRole(), req.senderId());

        // 일일 전송 제한 체크: 초과시 TOO_MANY_REQUESTS
        enforceDailyLimit(room.getId(), req.senderId());

        Chatting chatting = Chatting.create(room, req.message(), req.senderRole(), req.senderId());
        markReadForSelf(chatting);
        chattingRepository.save(chatting);

        return toResponse(room, chatting);
    }

    public void deleteMySideRoom(Accessor accessor, Long chatRoomId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST));
        String sid = accessor.getSocialId();
        if (sid.equals(room.getOwnerId())) {
            room.deleteByOwner();
            return;
        }
        if (sid.equals(room.getCustomerId())) {
            room.deleteByCustomer();
            return;
        }
        throw new BusinessException(ErrorCode.AUTH_FORBIDDEN);
    }

    // ---------- 내부 공통 유틸 ----------

    private void enforceDailyLimit(Long roomId, String senderId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.toLocalDate().atStartOfDay();
        LocalDateTime to = from.plusDays(1);
        long sent = chattingRepository.countSentBetween(roomId, senderId, from, to);
        if (sent >= ChatConstants.DAILY_SEND_LIMIT_PER_SIDE) {
            // 전송량 제한은 TOO_MANY_REQUESTS
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }
    }

    private void markReadForSelf(Chatting chatting) {
        LocalDateTime now = LocalDateTime.now();
        if (ChatConstants.ROLE_OWNER.equalsIgnoreCase(chatting.getSenderRole())) {
            chatting.readByOwner(now);
        } else if (ChatConstants.ROLE_CUSTOMER.equalsIgnoreCase(chatting.getSenderRole())) {
            chatting.readByCustomer(now);
        }
    }

    private ChatMessageResponse toResponse(ChatRoom room, Chatting chatting) {
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

    // (WS 경로 호환용) 기존 검증 로직 유지
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
}
