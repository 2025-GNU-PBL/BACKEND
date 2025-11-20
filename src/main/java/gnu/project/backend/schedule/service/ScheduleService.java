package gnu.project.backend.schedule.service;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_SOCIAL;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.RESERVATION_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.ROLE_IS_NOT_VALID;
import static gnu.project.backend.common.error.ErrorCode.SCHEDULE_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.provider.ScheduleFileProvider;
import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.repository.ReservationRepository;
import gnu.project.backend.schedule.dto.request.ScheduleEventRequestDto;
import gnu.project.backend.schedule.dto.request.ScheduleRequestDto;
import gnu.project.backend.schedule.dto.request.ScheduleUpdateRequestDto;
import gnu.project.backend.schedule.dto.response.ScheduleDateResponseDto;
import gnu.project.backend.schedule.dto.response.ScheduleResponseDto;
import gnu.project.backend.schedule.entity.Schedule;
import gnu.project.backend.schedule.entity.ScheduleFile;
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
    private final ReservationRepository reservationRepository;
    private final ScheduleFileProvider fileProvider;

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
            request.startScheduleDate(),
            request.endScheduleDate(),
            request.startTime(),
            request.endTime()
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
            request.startScheduleDate(),
            request.endScheduleDate(),
            request.startTime(),
            request.endTime()
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

    public ScheduleResponseDto updateSchedule(
        final Long id,
        final ScheduleUpdateRequestDto request,
        final Accessor accessor,
        final List<MultipartFile> files
    ) {
        final Schedule schedule = scheduleRepository.findScheduleById(id)
            .orElseThrow(() -> new BusinessException(SCHEDULE_NOT_FOUND_EXCEPTION));

        validateScheduleAccess(schedule, accessor);

        schedule.updateContent(
            request.title(),
            request.content(),
            request.startScheduleDate(),
            request.endScheduleDate(),
            request.startTime(),
            request.endTime()
        );

        if (files != null && !files.isEmpty()) {
            fileProvider.updateScheduleFiles(
                schedule,
                files,
                request.keepFileIds()
            );
        }

        return ScheduleResponseDto.toResponse(schedule);
    }

    public void deleteSchedule(final Long scheduleId, final Accessor accessor) {
        final Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new BusinessException(SCHEDULE_NOT_FOUND_EXCEPTION));

        validateScheduleAccess(schedule, accessor);

        final List<ScheduleFile> files = schedule.getFiles();
        if (!files.isEmpty()) {
            fileProvider.deleteFiles(files);
        }

        scheduleRepository.delete(schedule);
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


    public ScheduleResponseDto getSchedule(final Long id, final Accessor accessor) {
        final Schedule schedule = scheduleRepository.findScheduleById(id)
            .orElseThrow(() -> new BusinessException(SCHEDULE_NOT_FOUND_EXCEPTION));
        validateScheduleAccess(schedule, accessor);
        return ScheduleResponseDto.toResponse(schedule);
    }

    private void validateScheduleAccess(final Schedule schedule, final Accessor accessor) {
        switch (accessor.getUserRole()) {
            case CUSTOMER -> {
                if (schedule.getCustomer() == null ||
                    !accessor.getSocialId().equals(schedule.getCustomer().getSocialId())) {
                    throw new BusinessException(IS_NOT_VALID_SOCIAL);
                }
            }
            case OWNER -> {
                if (schedule.getOwner() == null ||
                    !accessor.getSocialId().equals(schedule.getOwner().getSocialId())) {
                    throw new BusinessException(IS_NOT_VALID_SOCIAL);
                }
            }
            default -> throw new BusinessException(ROLE_IS_NOT_VALID);
        }
    }

    public void createScheduleFromReservation(
        final ScheduleEventRequestDto scheduleRequestDto
    ) {
        if (scheduleRequestDto.reservationId() == null) {
            log.warn("예약 ID가 null입니다. 스케줄 생성을 건너뜁니다.");
            return;
        }

        if (scheduleRepository.existsByReservationId(scheduleRequestDto.reservationId())) {
            log.warn("이미 스케줄이 생성된 예약입니다 - Reservation ID: {}", scheduleRequestDto.reservationId());
            return;
        }

        final Reservation reservation = reservationRepository
            .findByIdWithAllRelations(scheduleRequestDto.reservationId())
            .orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND_EXCEPTION));

        final Owner owner = reservation.getOwner();
        final Customer customer = reservation.getCustomer();
        final Product product = reservation.getProduct();
        final Schedule schedule = Schedule.fromReservation(
            owner,
            customer,
            product,
            scheduleRequestDto.reservationId(),
            scheduleRequestDto.title(),
            scheduleRequestDto.content(),
            scheduleRequestDto.startScheduleDate(),
            scheduleRequestDto.endScheduleDate(),
            scheduleRequestDto.startTime(),
            scheduleRequestDto.endTime()

        );
        scheduleRepository.save(schedule);
    }
}

