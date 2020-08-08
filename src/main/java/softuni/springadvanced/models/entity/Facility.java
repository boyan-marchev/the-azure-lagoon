package softuni.springadvanced.models.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "facilities")
public class Facility extends BaseEntity{

    @NotNull
    @Column(unique = true)
    private String facilityName;

    @Column
    private String facilityType;

    @NotNull
    @Min(value = 1)
    @Column
    private int guestsCapacity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Min(value = 0)
    @Column
    private BigDecimal pricePerHour;

    @Transient
    private Map<LocalDate, Map<Integer, Integer>> availabilityPerDayAndHour = new TreeMap<>();

    @OneToMany(mappedBy = "facility", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();

    public Facility(String facilityName, int guestsCapacity, BigDecimal pricePerHour){
        this.facilityName = facilityName;
        this.guestsCapacity = guestsCapacity;
        this.pricePerHour = pricePerHour;

    }

    public Facility(String facilityName) {
        this.facilityName = facilityName;
    }

    public static Facility create(String facilityName, List<Event> events) {
        Facility newFacility = new Facility(facilityName, 1, BigDecimal.ZERO);

        events.forEach(event -> {
            event.setFacility(newFacility);
            newFacility.getEvents().add(event);
            String description = event.getEventName();
            event.setDescription(description);
            newFacility.setDescription(facilityName);
        });

        return newFacility;
    }

}
