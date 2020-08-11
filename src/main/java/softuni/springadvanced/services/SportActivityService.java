package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.models.entity.SportActivity;

import java.time.LocalDate;
import java.util.List;

public interface SportActivityService {

    void saveSportActivitiesInDatabase();

    void createAndSaveDefault();

    SportActivity getSportActivityByArt(String sportArt);

    List<SportActivity> getAllSportActivities();

    List<String> getSportActivityNames();

    boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Facility facility);
}
