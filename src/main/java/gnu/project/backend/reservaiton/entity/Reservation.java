package gnu.project.backend.reservaiton.entity;

import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.reservaiton.enumerated.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Product product;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private LocalDateTime reservationTime;

    @Column
    private String title;

    @Column
    private String content;

    public static Reservation ofCreate(
        final Owner owner,
        final Customer customer,
        final Product product,
        final Status status,
        final LocalDateTime reservationTime,
        final String title,
        final String content
    ) {
        Reservation reservation = new Reservation();
        reservation.owner = owner;
        reservation.customer = customer;
        reservation.product = product;
        reservation.status = status;
        reservation.reservationTime = reservationTime;
        reservation.title = title;
        reservation.content = content;
        return reservation;
    }

    public void changeStatus(final Status status) {
        this.status = status;
    }
}
