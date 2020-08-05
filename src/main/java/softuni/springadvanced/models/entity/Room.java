package softuni.springadvanced.models.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rooms")
public class Room extends BaseEntity implements Comparable<Room>{


    @NotNull
    @Column
    private String roomNumber;

    @NotNull
    @Column
    private String roomType;

    @NotNull
    @Min(value = 1)
    @Column
    private int guestsCapacity;

    @Transient
    private Map<String, List<LocalDateTime>> bookedDates = new TreeMap<>();

    @Column
    private boolean isAvailable = true;

    @NotNull
    @DecimalMin(value = "0")
    @Column
    private BigDecimal pricePerNight;

    @ManyToOne
    @ToString.Exclude
    private Hotel hotel;

    public Room(String roomNumber, String roomType, int guestsCapacity, BigDecimal pricePerNight){
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.guestsCapacity = guestsCapacity;
        this.pricePerNight = pricePerNight;
    }

    @Override
    public int compareTo(Room otherRoom) {
        return roomNumber.compareToIgnoreCase(otherRoom.getRoomNumber());
    }
}
