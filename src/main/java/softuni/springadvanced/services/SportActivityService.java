package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.SportActivity;

import java.util.List;

public interface SportActivityService {

    void saveSportActivitiesInDatabase();

    void createAndSaveDefault();

    SportActivity getSportActivityByArt(String sportArt);

    List<SportActivity> getAllSportActivities();
}
