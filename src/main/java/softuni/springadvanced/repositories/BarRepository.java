package softuni.springadvanced.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.springadvanced.models.entity.Bar;

import java.util.Optional;

@Repository
public interface BarRepository extends JpaRepository<Bar, String> {

    Optional<Bar> findByBarName(String barName);

}
