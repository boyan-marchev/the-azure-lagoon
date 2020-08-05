package softuni.springadvanced.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.springadvanced.models.entity.Hotel;

import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, String> {

    Optional<Hotel> findByHotelName(String hotelName);
}
