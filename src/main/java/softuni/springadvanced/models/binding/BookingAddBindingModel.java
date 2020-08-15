package softuni.springadvanced.models.binding;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingAddBindingModel {

    @NotNull(message = "This is a required field")
    private String userLastName;

    @NotNull(message = "This is a required field")
    private String facilityName;

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

    @NotNull(message = "This is a required field")
    @Min(value = 1)
    private int numberOfGuests;

}
