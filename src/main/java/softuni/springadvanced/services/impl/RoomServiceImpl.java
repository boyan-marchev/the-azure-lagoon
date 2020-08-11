package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Room;
import softuni.springadvanced.models.entity.RoomType;
import softuni.springadvanced.repositories.RoomRepository;
import softuni.springadvanced.services.HotelService;
import softuni.springadvanced.services.RoomService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }


    @Override
    public List<Room> getAllRooms() {
        return this.roomRepository.findAll();
    }

    @Override
    public Room getAvailableRoomByDates(List<Room> availableRooms, LocalDateTime startDate, LocalDateTime endDate, String bookingName) {
        if (availableRooms.size() < 1) {
            return null;
        }

        for (Room room : availableRooms) {
            if (room.getBookedDates().isEmpty()) {
                room.getBookedDates().putIfAbsent(bookingName, new ArrayList<>());
                room.getBookedDates().get(bookingName).add(0, startDate);
                room.getBookedDates().get(bookingName).add(1, endDate);

                return room;

            } else {
                for (Map.Entry<String, List<LocalDateTime>> entry : room.getBookedDates().entrySet()) {
                    List<LocalDateTime> dates = entry.getValue();
                    LocalDateTime start = dates.get(0);
                    LocalDateTime end = dates.get(1);

                    if (startDate.isBefore(start) && endDate.isBefore(start) ||
                            startDate.isAfter(end) || startDate.isAfter(start) && startDate.isAfter(end)) {
                        room.getBookedDates().putIfAbsent(bookingName, new ArrayList<>());
                        room.getBookedDates().get(bookingName).add(0, startDate);
                        room.getBookedDates().get(bookingName).add(1, endDate);

                        return room;

                    }
                }
            }
        }

        return null;
    }


}
