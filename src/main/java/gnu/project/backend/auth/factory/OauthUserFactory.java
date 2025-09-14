package gnu.project.backend.auth.factory;

import gnu.project.backend.auth.entity.OauthUser;
import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.auth.userinfo.OauthUserInfo;
import gnu.project.backend.common.enurmerated.UserRole;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OauthUserFactory {

    private final OwnerRepository ownerRepository;

    public OauthUser findOrCreateUser(OauthUserInfo userInfo, SocialProvider provider,
        UserRole userRole) {
        return switch (userRole) {
            case OWNER -> findOrCreateOwner(userInfo, provider);
            case CUSTOMER -> null;
            case ADMIN -> null;
            /*
            TODO : 추후 추가 예정
             */
        };
    }


    private Owner findOrCreateOwner(OauthUserInfo userInfo, SocialProvider provider) {
        return ownerRepository.findByOauthInfo_SocialId(userInfo.getSocialId())
            .orElseGet(() -> ownerRepository.save(
                Owner.signIn(userInfo.getEmail(), userInfo.getName(), userInfo.getSocialId(),
                    provider)
            ));
    }
}
