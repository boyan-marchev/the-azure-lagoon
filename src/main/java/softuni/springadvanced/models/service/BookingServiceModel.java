package softuni.springadvanced.models.service;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingServiceModel extends BaseServiceModel {

    @NotNull
    private String bookingName;

    @NotNull
    private String bookingType;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//    @PastOrPresent(message = "The date cannot be in the future")
    @FutureOrPresent(message = "The date cannot be in the past")
    @NotNull
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//    @FutureOrPresent(message = "The date cannot be in the past")
    @Future(message = "End date should be after the start date")
//    @NotNull
    private LocalDateTime endDate;


    @Min(value = 0)
    private BigDecimal price;

    @NotNull
    private String facilityName;

    @NotNull
    @Min(value = 1)
    private int numberOfGuests;

    @NotNull
    private UserServiceModel user;
}
