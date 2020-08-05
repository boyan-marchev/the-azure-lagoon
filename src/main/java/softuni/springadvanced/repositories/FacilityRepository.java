package softuni.springadvanced.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.springadvanced.models.entity.Facility;

import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {

    Optional<Facility> findByFacilityName(String facilityName);
}
