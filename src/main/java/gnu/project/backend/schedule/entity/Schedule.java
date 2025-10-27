package gnu.project.backend.schedule.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.entity.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate scheduleDate;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleFile> files = new ArrayList<>();

    public static Schedule ofCreate(
        final Owner owner,
        final Customer customer,
        final String title,
        final String content,
        final LocalDate scheduleDate
    ) {
        Schedule schedule = new Schedule();
        schedule.owner = owner;
        schedule.customer = customer;
        schedule.reservationId = null;
        schedule.product = null;
        schedule.title = title;
        schedule.content = content;
        schedule.scheduleDate = scheduleDate;
        return schedule;
    }

    public static Schedule fromReservation(
        final Owner owner,
        final Customer customer,
        final Product product,
        final Long reservationId,
        final String title,
        final String content,
        final LocalDate reservationTime
    ) {
        Schedule schedule = new Schedule();
        schedule.owner = owner;
        schedule.customer = customer;
        schedule.product = product;
        schedule.reservationId = reservationId;
        schedule.title = title;
        schedule.content = content;
        schedule.scheduleDate = reservationTime;
        return schedule;
    }

    public void addFiles(final ScheduleFile file) {
        this.files.add(file);
    }

    public void updateContent(
        final String title,
        final String content,
        final LocalDate scheduleDate
    ) {
        this.title = title;
        this.content = content;
        this.scheduleDate = scheduleDate;
    }
}
