package gnu.project.backend.owner.controller;


import gnu.project.backend.owner.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;
}
