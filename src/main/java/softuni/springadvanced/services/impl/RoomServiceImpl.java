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
import java.util.ArrayList;
import java.util.List;

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


}
