package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Restaurant;

import java.util.List;

public interface RestaurantService {

    void saveRestaurantsInDatabase();

    void createAndSaveDefault();

    Restaurant getRestaurantByName(String restaurantName);

    List<Restaurant> getAllRestaurants();

    List<String> getAllRestaurantsByName();
}
