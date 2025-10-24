package gnu.project.backend.schedule.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.schedule.dto.request.ScheduleRequestDto;
import gnu.project.backend.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping()
    public ResponseEntity<?> uploadSchedule(
        @RequestBody final ScheduleRequestDto request,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            scheduleService.upload(request, accessor)
        );
    }

}
