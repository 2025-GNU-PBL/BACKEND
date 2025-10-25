package gnu.project.backend.customer.entity;


import gnu.project.backend.auth.entity.OauthInfo;
import gnu.project.backend.auth.entity.OauthUser;
import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Customer")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity implements OauthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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

    @Embedded
    private OauthInfo oauthInfo;

    public static Customer signIn(
        final String email,
        final String name,
        final String socialId,
        final SocialProvider provider) {
        final OauthInfo oauthInfo = OauthInfo.of(email, name, socialId, provider);
        return new Customer(
                null,
                null,
                null,
                null,
                null,
                UserRole.CUSTOMER,
                oauthInfo);
    }


}
