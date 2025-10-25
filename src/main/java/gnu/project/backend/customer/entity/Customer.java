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

    @Column(name = "age")
    private Short age;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "is_deleted")
    private Boolean isDeleted;


    @Column
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Embedded
    private OauthInfo oauthInfo;


//    public static Customer signIn(
//            final String email,
//            final String name,
//            final String socialId,
//            final SocialProvider provider) {
//        final OauthInfo oauthInfo = OauthInfo.of(email, name, socialId, provider);
//
//        return Customer.builder()
//                .oauthInfo(oauthInfo)
//                .userRole(UserRole.CUSTOMER)
//                .build();
//    }

    public static Customer createFromOAuth(
            final String email,
            final String name,
            final String socialId,
            final SocialProvider provider
    ) {
        final OauthInfo oauthInfo = OauthInfo.of(email, name, socialId, provider);

        return new Customer(
                null,
                null,
                null,
                null,
                false,
                UserRole.CUSTOMER,
                oauthInfo

        );
    }

    public void signUp(
            final Short age,
            final String phoneNumber,
            final String address
    ) {
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void updateProfile(
            final Short age,
            final String phoneNumber,
            final String address
    ) {
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void withdraw() {
        this.isDeleted = true;
        this.phoneNumber = null;
        this.address = null;
        this.age = null;
    }

    public void reactivate() {
        this.isDeleted = false;
    }

    public boolean isActive() {
        return this.isDeleted == null || !this.isDeleted;
    }




}
