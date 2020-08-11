package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Restaurant;

import java.time.LocalDate;
import java.util.List;

public interface RestaurantService {

    void saveRestaurantsInDatabase();

    void createAndSaveDefault();

    Restaurant getRestaurantByName(String restaurantName);

    List<Restaurant> getAllRestaurants();

    List<String> getAllRestaurantsByName();

    boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Restaurant restaurant);

    void putMapToRestaurantIfAbsent(LocalDate askedDate, Restaurant restaurant, int hour);
}
