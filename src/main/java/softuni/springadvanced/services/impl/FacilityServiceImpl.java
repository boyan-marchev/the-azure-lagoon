package softuni.springadvanced.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.EventType;
import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.models.service.FacilityServiceModel;
import softuni.springadvanced.repositories.FacilityRepository;
import softuni.springadvanced.services.FacilityService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@Transactional
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public FacilityServiceImpl(FacilityRepository facilityRepository, ModelMapper modelMapper) {
        this.facilityRepository = facilityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void saveFacilityInDatabase(FacilityServiceModel facilityServiceModel) {
        Facility facility = this.modelMapper.map(facilityServiceModel, Facility.class);
        this.facilityRepository.saveAndFlush(facility);
    }

    @Override
    public Facility createAndSaveDefault(Facility newFacility) {

        if (newFacility.getFacilityName().equals("Congress Hall")){
            newFacility.setGuestsCapacity(80);
            newFacility.setPricePerHour(BigDecimal.valueOf(350));
            newFacility.setFacilityType(EventType.CONFERENCE.toString());

        }

        if (newFacility.getFacilityName().equals("Seminar room")){
            newFacility.setGuestsCapacity(50);
            newFacility.setPricePerHour(BigDecimal.valueOf(250));
            newFacility.setFacilityType(EventType.CONFERENCE.toString());

        }

        if (newFacility.getFacilityName().equals("Multifunctional Sport Hall")){
            newFacility.setGuestsCapacity(30);
            newFacility.setPricePerHour(BigDecimal.valueOf(60));
            newFacility.setFacilityType(EventType.SPORT.toString());

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

    @Override
    public List<String> getAllFacilityNames() {
        List<Facility> facilities = this.getAllFacilities();
        List<String> result = new ArrayList<>();

        for (Facility facility : facilities) {
            result.add(facility.getFacilityName());
        }

        return result;
    }

    @Override
    public List<Facility> getFacilityByType(String type) {
        List<Facility> allFacilities = this.getAllFacilities();
        List<Facility> result = new ArrayList<>();

        for (Facility facility : allFacilities) {
            if (facility.getFacilityType().equalsIgnoreCase(type)){
                result.add(facility);
            }
        }
        return result;
    }

    @Override
    public List<String> getAllFacilityTypes() {
        List<String> result = new ArrayList<>();
        for (EventType value : EventType.values()) {
            result.add(value.toString());
        }
        return result;
    }

    @Override
    public List<String> getSportFacilitiesNames() {
        List<Facility> allFacilities = this.getAllFacilities();
        List<String> result = new ArrayList<>();

        for (Facility facility : allFacilities) {
            if (facility.getFacilityType().equals(EventType.SPORT.toString())){
                result.add(facility.getFacilityName());
            }
        }

        return result;
    }

    @Override
    public void putMapToFacility(LocalDate askedDate, int hour, Facility facility) {
        if (facility.getAvailabilityPerDayAndHour() == null || facility.getAvailabilityPerDayAndHour().isEmpty()) {
            if (facility.getAvailabilityPerDayAndHour() != null) {
                facility.getAvailabilityPerDayAndHour().put(askedDate, new TreeMap<>());
            }
            if (facility.getAvailabilityPerDayAndHour() != null) {
                facility.getAvailabilityPerDayAndHour().get(askedDate)
                        .put(hour, facility.getGuestsCapacity());
            }
        }
    }

    @Override
    public boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Facility facility, int numberOfGuests) {
        return facility.getAvailabilityPerDayAndHour() == null ||
                facility.getAvailabilityPerDayAndHour().get(askedDate).get(hour) > 0 ||
                facility.getAvailabilityPerDayAndHour().get(askedDate).get(hour) >= numberOfGuests
                || facility.getAvailabilityPerDayAndHour().isEmpty();
    }

}
