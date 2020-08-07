package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Facility;

import java.util.List;

public interface FacilityService {

    void saveFacilitiesInDatabase();

    Facility createAndSaveDefault(Facility newFacility);

    Facility getFacilityByName(String facilityName);

    List<Facility> getAllFacilities();

    List<String> getAllFacilityNames();
}
