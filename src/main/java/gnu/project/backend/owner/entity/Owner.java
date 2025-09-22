package gnu.project.backend.owner.entity;

import gnu.project.backend.auth.entity.OauthInfo;
import gnu.project.backend.auth.entity.OauthUser;
import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enurmerated.UserRole;
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

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Owner extends BaseEntity implements OauthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String profileImage;

    @Column
    private Short age;

    @Column
    private String phoneNumber;

    @Column
    private String bzNumber;

    @Column
    private String bankAccount;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Embedded
    private OauthInfo oauthInfo;


    public static Owner createFromOAuth(
        final String email,
        final String name,
        final String socialId,
        final SocialProvider provider
    ) {
        final OauthInfo oauthInfo = OauthInfo.of(email, name, socialId, provider);
        return new Owner(null, null, null, null, null, null, UserRole.OWNER, oauthInfo);
    }

    public void signIn(
        final String profileImage,
        final Short age,
        final String phoneNumber,
        final String bzNumber,
        final String bankAccount
    ) {
        this.profileImage = profileImage;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.bzNumber = bzNumber;
        this.bankAccount = bankAccount;
    }

    public void updateProfile(
        final String profileImage,
        final Short age,
        final String phoneNumber,
        final String bzNumber,
        final String bankAccount
    ) {
        this.profileImage = profileImage;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.bzNumber = bzNumber;
        this.bankAccount = bankAccount;
    }
}
