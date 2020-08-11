package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Hotel;
import softuni.springadvanced.models.entity.Room;
import softuni.springadvanced.repositories.HotelRepository;
import softuni.springadvanced.services.HotelService;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }


    @Override
    public Hotel getHotelByName(String hotelName) {
        return this.hotelRepository.findByHotelName(hotelName).orElse(null);
    }

    @Override
    public List<Hotel> getAllHotels() {
        return this.hotelRepository.findAll();
    }


    @Override
    public Hotel createHotel(Hotel hotel) {
        int overallCapacity = 0;
        if (hotel.getDescription() == null){
            hotel.setDescription(hotel.getHotelName());
        }

        for (Room room : hotel.getRooms()) {
            int guestsCapacity = room.getGuestsCapacity();
            overallCapacity += guestsCapacity;
        }

        hotel.setOverallGuestsCapacity(overallCapacity);

        return this.hotelRepository.save(hotel);
    }

    @Override
    public List<Room> getRoomsByHotelNameAndGuestsNumber(String name, int number) {
        Hotel hotel = this.getHotelByName(name);

        List<Room> searched = new ArrayList<>();
        List<Room> allRooms = hotel.getRooms();

        for (Room room : allRooms) {
            if (room.getGuestsCapacity() == 2){
                searched.add(room);
            }
        }

        return searched;
    }

    @Override
    public List<String> allHotelsByName() {
        List<String> result = new ArrayList<>();
        List<Hotel> hotels = this.getAllHotels();

        for (Hotel hotel : hotels) {
            String name = hotel.getHotelName();
            result.add(name);
        }

        return result;
    }

    @Override
    public long getOvernights(LocalDateTime startDate, LocalDateTime endDate) {
        Duration duration = Duration.between(startDate, endDate);
        return duration.toDays();
    }


}
