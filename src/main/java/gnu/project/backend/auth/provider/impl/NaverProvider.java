package gnu.project.backend.auth.provider.impl;

import gnu.project.backend.auth.constant.NaverOauthConstants;
import gnu.project.backend.auth.dto.response.NaverAccessTokenResponse;
import gnu.project.backend.auth.dto.response.NaverUserInfoResponse;
import gnu.project.backend.auth.enumerated.SocialProvider;
import gnu.project.backend.auth.provider.OauthProvider;
import gnu.project.backend.auth.userinfo.NaverUserInfo;
import gnu.project.backend.auth.userinfo.OauthUserInfo;
import gnu.project.backend.common.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static gnu.project.backend.common.error.ErrorCode.OAUTH_TOKEN_REQUEST_FAILED;
import static gnu.project.backend.common.error.ErrorCode.OAUTH_USERINFO_RESPONSE_EMPTY;

@Component
@RequiredArgsConstructor
public class NaverProvider implements OauthProvider {

    private final WebClient webClient;

    @Value("${oauth.naver.client-id}")
    private String clientId;

    @Value("${oauth.naver.client-secret}")
    private String clientSecret;

    @Value("${oauth.naver.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.naver.token-uri}")
    private String tokenUri;

    @Value("${oauth.naver.user-info-uri}")
    private String userInfoUri;

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.NAVER;
    }

    @Override
    public String getAccessToken(String code) {
        final MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add(NaverOauthConstants.GRANT_TYPE_KEY, NaverOauthConstants.GRANT_TYPE);
        data.add(NaverOauthConstants.CLIENT_ID_KEY, clientId);
        data.add(NaverOauthConstants.CLIENT_SECRET_KEY, clientSecret);
        data.add(NaverOauthConstants.REDIRECT_URI_KEY, redirectUri);
        data.add(NaverOauthConstants.CODE_KEY, code);
        data.add(NaverOauthConstants.STATE_KEY, "STATE_STRING_FOR_TEST");

        return webClient.post()
            .uri(tokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(data))
            .retrieve()
            .onStatus(HttpStatusCode::isError, this::handleOauthError)
            .bodyToMono(NaverAccessTokenResponse.class)
            .map(NaverAccessTokenResponse::accessToken)
            .block();
    }

    @Override
    public OauthUserInfo getUserInfo(String accessToken) {
        return new NaverUserInfo(fetchUserInfo(accessToken));
    }

    private NaverUserInfoResponse fetchUserInfo(String accessToken) {
        return webClient
            .get() // ✅ 네이버는 사용자 정보 조회가 GET 방식
            .uri(userInfoUri)
            .header(HttpHeaders.AUTHORIZATION, NaverOauthConstants.BEARER_PREFIX + accessToken)
            .retrieve()
            .onStatus(HttpStatusCode::isError, this::handleOauthError)
            .bodyToMono(NaverUserInfoResponse.class)
            .blockOptional()
            .orElseThrow(() -> new AuthException(OAUTH_USERINFO_RESPONSE_EMPTY));
    }

    // Error Handling
    private Mono<? extends Throwable> handleOauthError(final ClientResponse response) {
        return response.bodyToMono(String.class)
            .flatMap(errorBody ->
                Mono.error(new AuthException(OAUTH_TOKEN_REQUEST_FAILED))
            );
    }
}
