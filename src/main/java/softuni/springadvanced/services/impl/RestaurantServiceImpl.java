package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.CuisineType;
import softuni.springadvanced.models.entity.Restaurant;
import softuni.springadvanced.repositories.RestaurantRepository;
import softuni.springadvanced.services.RestaurantService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public void saveRestaurantsInDatabase() {


    }

    @Override
    public void createAndSaveDefault() {
        createRestaurant("Florence", CuisineType.MEDITERRANEAN.toString(), 25, 25);
        createRestaurant("Azure Lagoon", CuisineType.INTERNATIONAL.toString(), 20, 20);
    }

    private void createRestaurant(String name, String cuisine, int capacity, int availableSeats) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName(name);
        restaurant.setCuisine(cuisine);
        restaurant.setSeatsCapacity(capacity);
        restaurant.setAvailableSeats(availableSeats);

        this.restaurantRepository.save(restaurant);

    }

    @Override
    public Restaurant getRestaurantByName(String restaurantName) {

        return this.restaurantRepository.findByRestaurantName(restaurantName).orElse(null);
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return this.restaurantRepository.findAll();
    }

    @Override
    public List<String> getAllRestaurantsByName() {
        List<Restaurant> restaurants = this.getAllRestaurants();
        List<String> result = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            result.add(restaurant.getRestaurantName());
        }

        return result;
    }

    @Override
    public boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Restaurant restaurant) {
        return restaurant.getAvailableSeatsPerDayAndHour() == null ||
                restaurant.getAvailableSeatsPerDayAndHour().get(askedDate).get(hour) > 0
                || restaurant.getAvailableSeatsPerDayAndHour().isEmpty();
    }

    @Override
    public void putMapToRestaurantIfAbsent(LocalDate askedDate, Restaurant restaurant, int hour) {
        if (restaurant.getAvailableSeatsPerDayAndHour() == null || restaurant.getAvailableSeatsPerDayAndHour().isEmpty()){
            if (restaurant.getAvailableSeatsPerDayAndHour() != null) {
                restaurant.getAvailableSeatsPerDayAndHour().put(askedDate, new TreeMap<>());
            }
            if (restaurant.getAvailableSeatsPerDayAndHour() != null) {
                restaurant.getAvailableSeatsPerDayAndHour().get(askedDate)
                        .put(hour, restaurant.getAvailableSeats());
            }
        }
    }
}
