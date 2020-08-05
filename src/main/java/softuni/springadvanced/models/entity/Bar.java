package softuni.springadvanced.models.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

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
@Table(name = "bars")
public class Bar extends BaseEntity{

    @NotNull
    @Length(min = 3, max = 20)
    @Column(unique = true)
    private String barName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Min(value = 0)
    private int seatsCapacity;

    @NotNull
    @Min(value = 0)
    private int availableSeats;

//    @NonNull
//    @Length(min = 8, max = 512)
//    private String imageUrl;
}
