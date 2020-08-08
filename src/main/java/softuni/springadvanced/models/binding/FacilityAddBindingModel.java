package softuni.springadvanced.models.binding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FacilityAddBindingModel {

    @NotNull
    private String facilityName;

    @NotNull
    private String facilityType;

    @NotNull
    @Min(value = 1)
    private int guestsCapacity;

    private String description;

    @NotNull
    @Min(value = 0)
    private BigDecimal pricePerHour;
}
