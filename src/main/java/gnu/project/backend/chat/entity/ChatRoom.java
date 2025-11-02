package gnu.project.backend.chat.entity;

import gnu.project.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "owner_deleted", nullable = false)
    private boolean ownerDeleted = false;

    @Column(name = "customer_deleted", nullable = false)
    private boolean customerDeleted = false;

    @Builder
    private ChatRoom(String ownerId, String customerId) {
        this.ownerId = ownerId;
        this.customerId = customerId;
    }

    public void deleteByOwner() {
        this.ownerDeleted = true;
    }

    public void deleteByCustomer() {
        this.customerDeleted = true;
    }
}
