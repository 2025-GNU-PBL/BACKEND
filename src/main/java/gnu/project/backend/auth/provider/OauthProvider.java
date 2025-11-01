package gnu.project.backend.auth.provider;

import gnu.project.backend.auth.enumerated.SocialProvider;
import gnu.project.backend.auth.userinfo.OauthUserInfo;

public interface OauthProvider {

    SocialProvider getProvider();

    //naver 추후 parmeter state 추가
    String getAccessToken(String code);

    OauthUserInfo getUserInfo(String accessToken);


}
