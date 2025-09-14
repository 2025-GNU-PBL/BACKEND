package gnu.project.backend.auth.service;

import gnu.project.backend.auth.dto.request.OauthLoginRequest;
import gnu.project.backend.auth.dto.response.AuthTokenDto;
import gnu.project.backend.auth.entity.OauthUser;
import gnu.project.backend.auth.factory.OauthUserFactory;
import gnu.project.backend.auth.provider.OauthProvider;
import gnu.project.backend.auth.provider.OauthProviders;
import gnu.project.backend.auth.userinfo.OauthUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService {

    private final OauthProviders oauthProviders;
    private final OauthUserFactory oauthUserFactory;

    public AuthTokenDto login(final OauthLoginRequest request) {
        final OauthProvider provider = oauthProviders.getProvider(request.socialProvider());
        final String accessToken = provider.getAccessToken(request.code());
        final OauthUserInfo userInfo = provider.getUserInfo(accessToken);
        final OauthUser user = oauthUserFactory.findOrCreateUser(
            userInfo,
            provider.getProvider(),
            request.userRole()
        );
        return null;
    }

}
