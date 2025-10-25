package gnu.project.backend.schedule.service;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_SOCIAL;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.ROLE_IS_NOT_VALID;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.provider.FileProvider;
import gnu.project.backend.schedule.dto.request.ScheduleRequestDto;
import gnu.project.backend.schedule.dto.response.ScheduleDateResponseDto;
import gnu.project.backend.schedule.dto.response.ScheduleResponseDto;
import gnu.project.backend.schedule.entity.Schedule;
import gnu.project.backend.schedule.repository.ScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final FileProvider fileProvider;

    // TODO : 삭제,수정 로직 구현
    public ScheduleResponseDto upload(
        final ScheduleRequestDto request,
        final Accessor accessor,
        final List<MultipartFile> files
    ) {
        switch (accessor.getUserRole()) {
            case OWNER -> {
                return uploadOwnerSchedule(request, accessor, files);
            }
            case CUSTOMER -> {
                return uploadCustomerSchedule(request, accessor, files);
            }
            default -> throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }
    }

    private ScheduleResponseDto uploadCustomerSchedule(
        final ScheduleRequestDto request,
        final Accessor accessor,
        final List<MultipartFile> files

    ) {
        final Customer customer = findCustomer(accessor);
        final Schedule schedule = Schedule.ofCreate(
            null,
            customer,
            request.title(),
            request.content(),
            request.scheduleDate()
        );

        final Schedule savedSchedule = scheduleRepository.save(schedule);

        if (files != null && !files.isEmpty()) {
            fileProvider.uploadAndSaveFiles(savedSchedule, files);
        }

        return ScheduleResponseDto.toResponse(savedSchedule);
    }

    private ScheduleResponseDto uploadOwnerSchedule(
        final ScheduleRequestDto request,
        final Accessor accessor,
        List<MultipartFile> files) {
        final Owner owner = findOwner(accessor);
        final Schedule schedule = Schedule.ofCreate(
            owner,
            null,
            request.title(),
            request.content(),
            request.scheduleDate()
        );

        final Schedule savedSchedule = scheduleRepository.save(schedule);

        if (files != null && !files.isEmpty()) {
            fileProvider.uploadAndSaveFiles(savedSchedule, files);
        }
        return ScheduleResponseDto.toResponse(savedSchedule);
    }

    private Owner findOwner(Accessor accessor) {
        return ownerRepository.findByOauthInfo_SocialId(
            accessor.getSocialId()
        ).orElseThrow(() -> new BusinessException(
            OWNER_NOT_FOUND_EXCEPTION)
        );
    }

    public List<ScheduleDateResponseDto> getSchedules(
        final Integer year,
        final Integer month,
        final Accessor accessor
    ) {
        switch (accessor.getUserRole()) {
            case OWNER -> {
                return getOwnerSchedules(year, month, accessor);
            }
            case CUSTOMER -> {
                return getCustomerSchedules(year, month, accessor);
            }
            default -> {
                throw new BusinessException(ROLE_IS_NOT_VALID);
            }
        }
    }

    private List<ScheduleDateResponseDto> getCustomerSchedules(
        final Integer year,
        final Integer month,
        final Accessor accessor) {
        final Customer customer = findCustomer(accessor);
        List<Schedule> schedules = scheduleRepository.findSchedulesById(
            customer.getId(),
            year,
            month,
            customer.getUserRole()
        );
        return schedules.stream().map(ScheduleDateResponseDto::toResponse).toList();
    }

    private List<ScheduleDateResponseDto> getOwnerSchedules(
        final Integer year,
        final Integer month,
        final Accessor accessor
    ) {
        final Owner owner = findOwner(accessor);
        List<Schedule> schedules = scheduleRepository.findSchedulesById(
            owner.getId(),
            year,
            month,
            owner.getUserRole()
        );
        return schedules.stream().map(ScheduleDateResponseDto::toResponse).toList();
    }

    private Customer findCustomer(Accessor accessor) {
        return customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));
    }


}
