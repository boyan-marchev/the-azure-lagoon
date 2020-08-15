package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.models.entity.SportActivity;
import softuni.springadvanced.models.entity.SportActivityArt;
import softuni.springadvanced.repositories.SportActivityRepository;
import softuni.springadvanced.services.FacilityService;
import softuni.springadvanced.services.SportActivityService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SportActivityServiceImpl implements SportActivityService {

    private final SportActivityRepository sportActivityRepository;
    private final FacilityService facilityService;

    @Autowired
    public SportActivityServiceImpl(SportActivityRepository sportActivityRepository, FacilityService facilityService) {
        this.sportActivityRepository = sportActivityRepository;
        this.facilityService = facilityService;
    }

    @Override
    public void saveSportActivitiesInDatabase() {
        // TODO: 14-Aug-20 admin functionality.
    }

    @Override
    public void createAndSaveDefault() {
        Arrays.stream(SportActivityArt.values())
                .forEach(sportActivityArt -> this.createSportActivity(sportActivityArt.toString()));

    }

    private void createSportActivity(String sportArt) {
        SportActivity sportActivity = new SportActivity();
        sportActivity.setSportArt(sportArt);
        sportActivity.setFacility(this.facilityService.getFacilityByName("Multifunctional Sport Hall"));

        this.sportActivityRepository.save(sportActivity);

    }

    @Override
    public SportActivity getSportActivityByArt(String sportArt) {
        return this.sportActivityRepository.findBySportArt(sportArt).orElse(null);
    }

    @Override
    public List<SportActivity> getAllSportActivities() {
        return this.sportActivityRepository.findAll();
    }

    @Override
    public List<String> getSportActivityNames() {
        List<SportActivity> activities = this.getAllSportActivities();
        List<String> result = new ArrayList<>();

        for (SportActivity activity : activities) {
            result.add(activity.getSportArt());
        }

        return result;
    }

    @Override
    public boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Facility facility) {
        return facility.getAvailabilityPerDayAndHour() == null ||
                facility.getAvailabilityPerDayAndHour().get(askedDate).get(hour) > 0
                || facility.getAvailabilityPerDayAndHour().isEmpty();
    }
}
