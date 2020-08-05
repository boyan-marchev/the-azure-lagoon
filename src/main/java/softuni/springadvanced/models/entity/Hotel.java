package softuni.springadvanced.models.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hotels")
public class Hotel extends BaseEntity{

    @NotNull
    @Column(unique = true)
    private String hotelName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    @NotNull
    @Min(value = 1)
    private int overallGuestsCapacity;

    @NotNull
    @Min(value = 0)
    private int availableRooms;

    public Hotel(String hotelName, int overallGuestsCapacity, int availableRooms){
        this.hotelName = hotelName;
        this.overallGuestsCapacity = overallGuestsCapacity;
        this.availableRooms = availableRooms;
    }


    public static Hotel create(String name, List<Room> rooms) {
        Hotel hotel = new Hotel(name, rooms.size(), rooms.size());

        rooms.stream().sorted().forEach(room -> {
            room.setHotel(hotel);
            hotel.getRooms().add(room);
        });

        return hotel;
    }
}
