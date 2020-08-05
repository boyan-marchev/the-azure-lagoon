package softuni.springadvanced.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import softuni.springadvanced.models.entity.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

}
