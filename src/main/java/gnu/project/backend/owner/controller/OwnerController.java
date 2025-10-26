package gnu.project.backend.owner.controller;


import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.dto.UploadImageDto;
import gnu.project.backend.owner.controller.docs.OwnerDocs;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/owner")
public class OwnerController implements OwnerDocs {

    private final OwnerService ownerService;

    @Override
    @PostMapping()
    public ResponseEntity<OwnerSignInResponse> signIn(
        @Auth final Accessor accessor,
        @RequestBody final OwnerRequest signInRequest
    ) {
        return ResponseEntity.ok(ownerService.signUp(accessor, signInRequest));
    }

    @Override
    @PostMapping("/profile/image")
    public ResponseEntity<UploadImageDto> uploadImage(
        @Auth final Accessor accessor,
        @RequestParam(name = "file") final MultipartFile file
    ) {
        return ResponseEntity.ok(
            ownerService.uploadProfileImage(
                accessor,
                file
            )
        );
    }

    @Override
    @PatchMapping()
    public ResponseEntity<OwnerUpdateResponse> update(
        @Auth final Accessor accessor,
        @RequestBody final OwnerRequest updateRequest
    ) {
        return ResponseEntity.ok(ownerService.update(accessor, updateRequest));
    }

    @Override
    @GetMapping()
    public ResponseEntity<OwnerResponse> find(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(ownerService.findOwner(accessor));
    }


}
