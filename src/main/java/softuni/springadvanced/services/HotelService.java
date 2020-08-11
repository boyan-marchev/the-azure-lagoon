package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Hotel;
import softuni.springadvanced.models.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface HotelService {

    Hotel getHotelByName(String hotelName);

    List<Hotel> getAllHotels();

    Hotel createHotel(Hotel hotel);

    List<Room> getRoomsByHotelNameAndGuestsNumber(String name, int number);

    List<String> allHotelsByName();

    long getOvernights(LocalDateTime startDate, LocalDateTime endDate);
}
