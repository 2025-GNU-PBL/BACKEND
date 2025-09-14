package gnu.project.backend.owner.entity;

import gnu.project.backend.auth.entity.OauthInfo;
import gnu.project.backend.auth.entity.OauthUser;
import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    @Embedded
    private OauthInfo oauthInfo;

    public static Owner signIn(
        final String email,
        final String name,
        final String socialId,
        final SocialProvider provider
    ) {
        final OauthInfo oauthInfo = OauthInfo.of(email, name, socialId, provider);
        return new Owner(null, null, null, null, null, null, oauthInfo);
    }
}
