package gnu.project.backend.auth.controller;

import gnu.project.backend.auth.dto.request.OauthLoginRequest;
import gnu.project.backend.auth.dto.response.AuthTokenDto;
import gnu.project.backend.auth.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class OauthController {

    private final OauthService oauthService;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenDto> login(@RequestBody final OauthLoginRequest request) {
        return ResponseEntity.ok(oauthService.login(request));
    }
}
