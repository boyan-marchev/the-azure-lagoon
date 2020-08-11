package softuni.springadvanced.models.binding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class FacilityAddBindingModel {

    @NotNull
    @Length(min = 3)
    private String facilityName;

    @NotNull
    private String facilityType;

    @NotNull
    @Min(value = 1)
    private int guestsCapacity;

    private String description;

    @NotNull
    @Min(value = 1)
    private BigDecimal pricePerHour;
}
