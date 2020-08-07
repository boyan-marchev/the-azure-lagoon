package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.SportActivity;
import softuni.springadvanced.repositories.SportActivityRepository;
import softuni.springadvanced.services.SportActivityService;

import java.util.ArrayList;
import java.util.List;

@Service
public class SportActivityServiceImpl implements SportActivityService {

    private final SportActivityRepository sportActivityRepository;

    @Autowired
    public SportActivityServiceImpl(SportActivityRepository sportActivityRepository) {
        this.sportActivityRepository = sportActivityRepository;
    }

    @Override
    public void saveSportActivitiesInDatabase() {

    }

    @Override
    public void createAndSaveDefault() {
        createSportActivity("Volleyball");
        createSportActivity("Football");
        createSportActivity("Tennis");
        createSportActivity("Fitness");
        createSportActivity("Swimming");

    }

    private void createSportActivity(String sportArt) {

        SportActivity sportActivity = new SportActivity();
        sportActivity.setSportArt(sportArt);

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
}
