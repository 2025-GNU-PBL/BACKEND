package gnu.project.backend.owner.controller;


import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.owner.dto.request.OwnerRequest;
import gnu.project.backend.owner.dto.response.OwnerResponse;
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


    @PostMapping()
    public ResponseEntity<OwnerSignInResponse> signIn(
        @Auth final Accessor accessor,
        @RequestBody final OwnerRequest signInRequest
    ) {
        return ResponseEntity.ok(ownerService.signIn(accessor, signInRequest));
    }

    @PatchMapping()
    public ResponseEntity<OwnerUpdateResponse> update(
        @Auth final Accessor accessor,
        @RequestBody final OwnerRequest updateRequest
    ) {
        return ResponseEntity.ok(ownerService.update(accessor, updateRequest));
    }

    @GetMapping()
    public ResponseEntity<OwnerResponse> find(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(ownerService.findOwner(accessor));
    }


}
