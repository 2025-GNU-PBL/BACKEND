package gnu.project.backend.auth.jwt;

import static gnu.project.backend.auth.constant.JwtConstants.TOKEN_TYPE;
import static gnu.project.backend.auth.constant.JwtConstants.USER_ROLE;

import gnu.project.backend.auth.enumerated.TokenType;
import gnu.project.backend.common.enumerated.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String createAccessToken(final String socialId, final UserRole userRole) {
        return createToken(
            socialId,
            jwtProperties.getAccessTokenExpirationMillis(),
            TokenType.ACCESS_TOKEN,
            userRole
        );
    }

    // TODO: 추후 사용
    public String createRefreshToken(final String socialId, final UserRole userRole) {
        return createToken(
            socialId,
            jwtProperties.getAccessTokenExpirationMillis(),
            TokenType.REFRESH_TOKEN,
            userRole
        );
    }

    private String createToken(
        final String socialId,
        final long expirationMillis,
        final TokenType tokenType,
        final UserRole userRole
    ) {
        final Date now = new Date();
        final Date expiredDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
            .setSubject(socialId)
            .claim(USER_ROLE, userRole)
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .claim(TOKEN_TYPE, tokenType.name())
            .signWith(jwtProperties.getSecretKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}
