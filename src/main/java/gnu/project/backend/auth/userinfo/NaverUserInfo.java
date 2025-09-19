package gnu.project.backend.auth.userinfo;

import gnu.project.backend.auth.dto.response.NaverUserInfoResponse;

public class NaverUserInfo implements OauthUserInfo {

    private final NaverUserInfoResponse naverResponse; // 네이버의 원본 응답 DTO

    // 생성자에서 네이버 응답 DTO를 받습니다.
    public NaverUserInfo(NaverUserInfoResponse response) {
        this.naverResponse = response;
    }

    @Override
    public String getSocialId() {
        // 네이버의 중첩된 구조('response' 객체)에서 id를 꺼내 반환합니다.
        return naverResponse.response().id();
    }

    @Override
    public String getEmail() {
        // 네이버의 중첩된 구조에서 email을 꺼내 반환합니다.
        return naverResponse.response().email();
    }

    @Override
    public String getName() {
        // 네이버의 중첩된 구조에서 name을 꺼내 반환합니다.
        return naverResponse.response().name();
    }
}
