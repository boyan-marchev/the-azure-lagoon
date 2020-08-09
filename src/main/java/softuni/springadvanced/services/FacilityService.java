package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.models.service.FacilityServiceModel;

import java.util.List;

public interface FacilityService {

    void saveFacilityInDatabase(FacilityServiceModel facilityServiceModel);

    Facility createAndSaveDefault(Facility newFacility);

    Facility getFacilityByName(String facilityName);

    List<Facility> getAllFacilities();

    List<String> getAllFacilityNames();

    List<Facility> getFacilityByType(String type);

    List<String> getAllFacilityTypes();

    List<String> getSportFacilitiesNames();
}
