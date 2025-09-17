package gnu.project.backend.owner.controller;


import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.owner.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

}
