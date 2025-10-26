package gnu.project.backend.reservation.event;

import gnu.project.backend.schedule.dto.request.ScheduleEventRequestDto;
import gnu.project.backend.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final ScheduleService scheduleService;

    @Async
    @EventListener
    @Transactional
    public void handleReservationApproved(final ReservationApprovedEvent event) {
        final ScheduleEventRequestDto scheduleRequestDto = new ScheduleEventRequestDto(
            event.reservationId(),
            event.reservationTime(),
            event.title(),
            event.content()
        );
        scheduleService.createScheduleFromReservation(
            scheduleRequestDto
        );
    }

}
