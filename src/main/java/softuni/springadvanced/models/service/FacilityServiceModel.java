package softuni.springadvanced.models.service;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FacilityServiceModel extends BaseServiceModel{

    @NotNull
    private String facilityName;

    @NotNull
    @Min(value = 1)
    private int guestsCapacity;


    private String description;

    @NotNull
    @Min(value = 0)
    private BigDecimal pricePerHour;
}
