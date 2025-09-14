package gnu.project.backend.auth.provider;

import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.auth.userinfo.OauthUserInfo;

public interface OauthProvider {

    SocialProvider getProvider();

    String getAccessToken(String code);

    OauthUserInfo getUserInfo(String accessToken);


}
