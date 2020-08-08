package softuni.springadvanced.models.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity{

    @NotNull
    @Column
    private String bookingName;

    @NotNull
    @Column
    private String bookingType;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//    @PastOrPresent(message = "The date cannot be in the future")
    @FutureOrPresent(message = "The date cannot be in the past")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//    @FutureOrPresent(message = "The date cannot be in the past")
    @Future(message = "End date should be after the start date")
    @Column(name = "end_date")
//    @NotNull
    private LocalDateTime endDate;


    @Min(value = 0)
    @Column
    private BigDecimal price;

    @NotNull
    @Column
    private String facilityName;

    @NotNull
    @Min(value = 1)
    private int numberOfGuests;

    @ManyToOne
    @ToString.Exclude
    private User user;

    @Column
    private boolean isConfirmed = false;
}
