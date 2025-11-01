// src/main/java/gnu/project/backend/chat/entity/Chatting.java
package gnu.project.backend.chat.entity;

import gnu.project.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatting extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false, length = 1000)
    private String message;

    // 누가 보냈는지 역할
    @Column(name = "sender_role", nullable = false, length = 50)
    private String senderRole; // OWNER | CUSTOMER

    // 실제 사용자 식별자 (email, userId 등)
    @Column(name = "sender_id", nullable = false, length = 255)
    private String senderId;

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    @Column(name = "owner_read", nullable = false)
    private boolean ownerRead;

    @Column(name = "owner_read_at")
    private LocalDateTime ownerReadAt;

    @Column(name = "customer_read", nullable = false)
    private boolean customerRead;

    @Column(name = "customer_read_at")
    private LocalDateTime customerReadAt;

    @Builder
    private Chatting(ChatRoom chatRoom,
                     String message,
                     String senderRole,
                     String senderId,
                     LocalDateTime sendTime,
                     boolean ownerRead,
                     LocalDateTime ownerReadAt,
                     boolean customerRead,
                     LocalDateTime customerReadAt) {
        this.chatRoom = chatRoom;
        this.message = message;
        this.senderRole = senderRole;
        this.senderId = senderId;
        this.sendTime = sendTime;
        this.ownerRead = ownerRead;
        this.ownerReadAt = ownerReadAt;
        this.customerRead = customerRead;
        this.customerReadAt = customerReadAt;
    }

    public void readByOwner(LocalDateTime now) {
        this.ownerRead = true;
        this.ownerReadAt = now;
    }

    public void readByCustomer(LocalDateTime now) {
        this.customerRead = true;
        this.customerReadAt = now;
    }
}
