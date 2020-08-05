package softuni.springadvanced.models.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    @Min(value = 1)
    @Column
    private int seatsCapacity;

    @Min(value = 0)
    @Column
    private int availableSeats;

}
