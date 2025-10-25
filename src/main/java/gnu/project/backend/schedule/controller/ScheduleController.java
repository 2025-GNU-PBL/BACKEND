package gnu.project.backend.schedule.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.schedule.dto.request.ScheduleRequestDto;
import gnu.project.backend.schedule.dto.request.ScheduleUpdateRequestDto;
import gnu.project.backend.schedule.dto.response.ScheduleDateResponseDto;
import gnu.project.backend.schedule.dto.response.ScheduleResponseDto;
import gnu.project.backend.schedule.service.ScheduleService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping()
    public ResponseEntity<ScheduleResponseDto> uploadSchedule(
        @RequestPart(name = "request") final ScheduleRequestDto request,
        @RequestPart(name = "file", required = false) final List<MultipartFile> file,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            scheduleService.upload(request, accessor, file)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getSchedule(
        @PathVariable(name = "id") final Long id,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            scheduleService.getSchedule(
                id,
                accessor
            )
        );
    }

    @GetMapping()
    public ResponseEntity<List<ScheduleDateResponseDto>> getSchedules(
        @RequestParam @NotNull @Min(2000) final Integer year,
        @RequestParam @NotNull @Min(1) @Max(12) final Integer month,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            scheduleService.getSchedules(year, month, accessor)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
        @PathVariable final Long id,
        @RequestPart("request") final ScheduleUpdateRequestDto request,
        @RequestPart(value = "file", required = false) final List<MultipartFile> files,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            scheduleService.updateSchedule(id, request, accessor, files)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
        @PathVariable final Long id,
        @Auth final Accessor accessor
    ) {
        scheduleService.deleteSchedule(id, accessor);
        return ResponseEntity.noContent().build();
    }
}
