package softuni.springadvanced.models.service;

import lombok.*;
import softuni.springadvanced.models.entity.Event;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FacilityServiceModel extends BaseServiceModel{

    @NotNull
    private String facilityName;

    private String facilityType;

    @NotNull
    @Min(value = 1)
    private int guestsCapacity;


    private String description;

    @NotNull
    @Min(value = 0)
    private BigDecimal pricePerHour;

    private Map<LocalDate, Map<Integer, Integer>> availabilityPerDayAndHour = new TreeMap<>();

    private List<Event> events = new ArrayList<>();

}
