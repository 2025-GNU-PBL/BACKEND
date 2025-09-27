package gnu.project.backend.auth.jwt;

import static gnu.project.backend.auth.constant.JwtConstants.TOKEN_TYPE;
import static gnu.project.backend.auth.constant.JwtConstants.USER_ROLE;
import static gnu.project.backend.common.error.ErrorCode.AUTH_TOKEN_EXPIRED;
import static gnu.project.backend.common.error.ErrorCode.AUTH_TOKEN_INVALID;

import gnu.project.backend.auth.enurmerated.TokenType;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.common.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtResolver {

    private final JwtProperties jwtProperties;

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractSocialId(String token) {
        return (parseClaims(token).getSubject());
    }

    public UserRole extractUserRole(String token) {
        String value = parseClaims(token).get(USER_ROLE, String.class);
        return UserRole.valueOf(value);
    }

    // TODO : 추후 사용
    public TokenType extractTokenType(String token) {
        String value = parseClaims(token).get(TOKEN_TYPE, String.class);
        return TokenType.valueOf(value);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthException(AUTH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AUTH_TOKEN_INVALID);
        }
    }
}
