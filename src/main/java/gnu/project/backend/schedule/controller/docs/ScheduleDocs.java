package gnu.project.backend.schedule.controller.docs;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.schedule.dto.request.ScheduleRequestDto;
import gnu.project.backend.schedule.dto.request.ScheduleUpdateRequestDto;
import gnu.project.backend.schedule.dto.response.ScheduleDateResponseDto;
import gnu.project.backend.schedule.dto.response.ScheduleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Schedule API", description = "일정 관련 API")
public interface ScheduleDocs {

    @Operation(
        summary = "일정 등록",
        description = "새로운 일정을 등록합니다. 이미지 파일 업로드가 선택적으로 가능합니다."
    )
    @PostMapping()
    ResponseEntity<ScheduleResponseDto> uploadSchedule(
        @Parameter(description = "등록할 일정 정보") @Valid @RequestPart("request") ScheduleRequestDto request,
        @Parameter(description = "업로드할 이미지 파일 (선택)") @RequestPart(value = "file", required = false) List<MultipartFile> file,
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "일정 수정",
        description = "기존 일정 정보를 수정합니다. 기존 이미지를 유지하거나 새로운 이미지를 추가할 수 있습니다."
    )
    @PatchMapping("/{id}")
    ResponseEntity<ScheduleResponseDto> updateSchedule(
        @Parameter(description = "수정할 일정 ID") @PathVariable Long id,
        @Parameter(description = "수정할 일정 정보") @RequestPart("request") ScheduleUpdateRequestDto request,
        @Parameter(description = "업로드할 이미지 파일 (선택)") @RequestPart(value = "file", required = false) List<MultipartFile> files,
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "일정 단건 조회",
        description = "특정 일정의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    ResponseEntity<ScheduleResponseDto> getSchedule(
        @Parameter(description = "조회할 일정 ID") @PathVariable Long id,
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "월별 일정 목록 조회",
        description = "지정한 연도와 월의 일정 목록을 조회합니다."
    )
    @GetMapping()
    ResponseEntity<List<ScheduleDateResponseDto>> getSchedules(
        @Parameter(description = "조회할 연도") @RequestParam @NotNull @Min(2000) Integer year,
        @Parameter(description = "조회할 월") @RequestParam @NotNull @Min(1) @Max(12) Integer month,
        @Parameter(hidden = true) Accessor accessor
    );

    @Operation(
        summary = "일정 삭제",
        description = "특정 일정을 삭제합니다."
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSchedule(
        @Parameter(hidden = true) Accessor accessor,
        @Parameter(description = "삭제할 일정 ID") @PathVariable Long id
    );
}
