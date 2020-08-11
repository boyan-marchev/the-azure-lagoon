package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService {

    List<Room> getAllRooms();

    Room getAvailableRoomByDates(List<Room> availableRooms, LocalDateTime startDate, LocalDateTime endDate, String bookingName);
}
