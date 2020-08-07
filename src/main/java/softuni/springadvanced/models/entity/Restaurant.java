package softuni.springadvanced.models.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "restaurants")
public class Restaurant extends BaseEntity{

    @NotNull
    @Column(unique = true)
    private String restaurantName;

    @NotNull
    @Column
    private String cuisine;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Transient
    private Map<LocalDate, Map<Integer, Integer>> availableSeatsPerDayAndHour = new TreeMap<>();

    @Min(value = 1)
    @Column
    private int seatsCapacity;

    @Min(value = 0)
    @Column
    private int availableSeats;

}
