package softuni.springadvanced.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.springadvanced.models.entity.Booking;
import softuni.springadvanced.models.entity.User;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findById(String id);

    Optional<Booking> findByBookingName(String bookingName);

    Optional<Booking> findByUser(User user);

}
