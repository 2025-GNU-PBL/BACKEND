package gnu.project.backend.owner.controller;


import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.owner.dto.request.OwnerSignInRequest;
import gnu.project.backend.owner.dto.request.OwnerUpdateRequest;
import gnu.project.backend.owner.dto.response.OwnerSignInResponse;
import gnu.project.backend.owner.dto.response.OwnerUpdateResponse;
import gnu.project.backend.owner.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/owner")
public class OwnerController {

    private final OwnerService ownerService;

    /**
     * @param accessor - jwt 토큰에 저장된 메타데이터를 객체화한것
     * @Auth 사용 예제
     * @author Hong
     */
    @GetMapping()
    public ResponseEntity<?> read(@Auth final Accessor accessor) {
        return ResponseEntity.ok(ownerService.read(accessor));
    }

    @PostMapping()
    public ResponseEntity<OwnerSignInResponse> signIn(
        @Auth final Accessor accessor,
        @RequestBody final OwnerSignInRequest signInRequest
    ) {
        return ResponseEntity.ok(ownerService.signInOwner(accessor, signInRequest));
    }

    @PatchMapping()
    public ResponseEntity<OwnerUpdateResponse> update(
        @Auth final Accessor accessor,
        @RequestBody final OwnerUpdateRequest updateRequest
    ) {
        return ResponseEntity.ok(ownerService.updateOwner(accessor, updateRequest));
    }

}
