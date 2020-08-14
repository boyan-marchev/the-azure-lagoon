package softuni.springadvanced.models.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sport_activities")
public class SportActivity extends BaseEntity{

    @NotNull
    @Column(unique = true)
    private String sportArt;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private Facility facility;

//    @NotNull
//    @Length(min = 8, max = 512)
//    private String imageUrl;
}
