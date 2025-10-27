package gnu.project.backend.customer.entity;


import gnu.project.backend.auth.entity.OauthInfo;
import gnu.project.backend.auth.entity.OauthUser;
import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Customer")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity implements OauthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Embedded
    private OauthInfo oauthInfo;

    @Column(name = "age")
    private Short age;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

//    @Embedded
//    private OauthInfo oauthInfo;

    public static Customer signIn(
            final String email,
            final String name,
            final String socialId,
            final SocialProvider provider) {
        final OauthInfo oauthInfo = OauthInfo.of(email, name, socialId, provider);

        return Customer.builder()
                .oauthInfo(oauthInfo)
                .userRole(UserRole.CUSTOMER)
                .build();
    }


}
