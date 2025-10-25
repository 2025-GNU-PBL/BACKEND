package gnu.project.backend.schedule.repository.impl;

import static gnu.project.backend.schedule.entity.QSchedule.schedule;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.schedule.entity.Schedule;
import gnu.project.backend.schedule.repository.ScheduleCustomRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleCustomRepository {

    private static final OrderSpecifier<?>[] SCHEDULE_DEFAULT_ORDER = {
        schedule.scheduleDate.asc(),
        schedule.id.asc()
    };
    private final JPAQueryFactory query;

    @Override
    public List<Schedule> findSchedulesById(
        final Long id,
        final Integer year,
        final Integer month,
        final UserRole userRole
    ) {
        final LocalDate startDate = LocalDate.of(year, month, 1);
        final LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        switch (userRole) {
            case OWNER -> {
                return findOwnerSchedules(id, startDate, endDate);
            }
            case CUSTOMER -> {
                return findCustomerSchedules(id, startDate, endDate);
            }
        }
        return List.of();
    }

    private List<Schedule> findCustomerSchedules(Long id, LocalDate startDate, LocalDate endDate) {
        return query.selectFrom(schedule)
            .where(
                schedule.customer.id.eq(id),
                schedule.scheduleDate.goe(startDate),
                schedule.scheduleDate.loe(endDate)
            )
            .orderBy(SCHEDULE_DEFAULT_ORDER)
            .fetch();
    }

    private List<Schedule> findOwnerSchedules(Long id, LocalDate startDate, LocalDate endDate) {
        return query.selectFrom(schedule)
            .where(
                schedule.owner.id.eq(id),
                schedule.scheduleDate.goe(startDate),
                schedule.scheduleDate.loe(endDate)
            )
            .orderBy(SCHEDULE_DEFAULT_ORDER)
            .fetch();
    }
}
