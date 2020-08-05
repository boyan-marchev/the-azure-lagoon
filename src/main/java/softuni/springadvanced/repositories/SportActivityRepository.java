package softuni.springadvanced.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.springadvanced.models.entity.SportActivity;

import java.util.Optional;

@Repository
public interface SportActivityRepository extends JpaRepository<SportActivity, String> {

    Optional<SportActivity> findBySportArt(String sportArt);
}
