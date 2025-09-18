package gnu.project.backend.auth.service;

import static gnu.project.backend.common.error.ErrorCode.AUTH_USER_NOT_FOUND;

import gnu.project.backend.auth.dto.request.OauthLoginRequest;
import gnu.project.backend.auth.dto.response.AuthTokenDto;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.auth.entity.OauthUser;
import gnu.project.backend.auth.factory.OauthUserFactory;
import gnu.project.backend.auth.jwt.JwtProvider;
import gnu.project.backend.auth.provider.OauthProvider;
import gnu.project.backend.auth.provider.OauthProviders;
import gnu.project.backend.auth.userinfo.OauthUserInfo;
import gnu.project.backend.common.enurmerated.UserRole;
import gnu.project.backend.common.exception.AuthException;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService {

    private final OauthProviders oauthProviders;
    private final OwnerRepository ownerRepository;
    private final OauthUserFactory oauthUserFactory;
    private final JwtProvider jwtProvider;
    private final CustomerRepository customerRepository;

    public AuthTokenDto login(final OauthLoginRequest request) {
        final OauthProvider provider = oauthProviders.getProvider(request.socialProvider());
        final String accessToken = provider.getAccessToken(request.code());
        final OauthUserInfo userInfo = provider.getUserInfo(accessToken);
        final OauthUser user = oauthUserFactory.findOrCreateUser(
            userInfo,
            provider.getProvider(),
            request.userRole()
        );
        return AuthTokenDto.of(
            jwtProvider.createAccessToken(user.getSocialId(), user.getUserRole())
        );
    }

    public Accessor getCurrentAccessor(final String socialId, final UserRole userRole) {
        log.debug("Getting accessor for socialId: {}, userRole: {}", socialId, userRole);
        
        if (!isUserExists(socialId, userRole)) {
            log.warn("User not found for socialId: {}, userRole: {}", socialId, userRole);
            throw new AuthException(AUTH_USER_NOT_FOUND);
        }

        return Accessor.user(socialId, userRole);
    }

    private boolean isUserExists(String socialId, UserRole userRole) {
        return switch (userRole) {
            case OWNER -> ownerRepository.existsByOauthInfo_SocialId(socialId);
            case CUSTOMER -> customerRepository.existsByOauthInfo_SocialId(socialId);
            default -> false;
        };
    }

}
