package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.repositories.FacilityRepository;
import softuni.springadvanced.services.FacilityService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;

    @Autowired
    public FacilityServiceImpl(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    public void saveFacilitiesInDatabase() {
// admin functionality
    }

    @Override
    public Facility createAndSaveDefault(Facility newFacility) {

        if (newFacility.getFacilityName().equals("Congress Hall")){
            newFacility.setGuestsCapacity(80);
            newFacility.setPricePerHour(BigDecimal.valueOf(350));

        }

        if (newFacility.getFacilityName().equals("Seminar room")){
            newFacility.setGuestsCapacity(50);
            newFacility.setPricePerHour(BigDecimal.valueOf(250));

        }

        if (newFacility.getFacilityName().equals("Multifunctional Sport Hall")){
            newFacility.setGuestsCapacity(30);
            newFacility.setPricePerHour(BigDecimal.valueOf(60));

        }

        return this.facilityRepository.save(newFacility);
    }


    @Override
    public Facility getFacilityByName(String facilityName) {
        return this.facilityRepository.findByFacilityName(facilityName).orElse(null);
    }

    @Override
    public List<Facility> getAllFacilities() {
        return this.facilityRepository.findAll();
    }

}
