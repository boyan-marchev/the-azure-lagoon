package softuni.springadvanced.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import softuni.springadvanced.models.entity.*;
import softuni.springadvanced.services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class Initialize implements CommandLineRunner {

    private final CategoryService categoryService;
    private final RoleService roleService;
    private final BarService barService;
    private final FacilityService facilityService;
    private final HotelService hotelService;
    private final RestaurantService restaurantService;
    private final SportActivityService sportActivityService;

    @Autowired
    public Initialize(CategoryService categoryService, RoleService roleService, BarService barService, FacilityService facilityService, HotelService hotelService, RestaurantService restaurantService, SportActivityService sportActivityService) {
        this.categoryService = categoryService;
        this.roleService = roleService;
        this.barService = barService;
        this.facilityService = facilityService;
        this.hotelService = hotelService;
        this.restaurantService = restaurantService;
        this.sportActivityService = sportActivityService;
    }

    private static final Map<String, List<Room>> SAMPLE_HOTELS = Map.of("Premium", List.of(
            new Room("101", RoomType.DOUBLE_DELUXE.toString(), 2, BigDecimal.valueOf(90)),
            new Room("102", RoomType.DOUBLE_DELUXE.toString(), 2, BigDecimal.valueOf(90)),
            new Room("103", RoomType.DOUBLE_DELUXE.toString(), 2, BigDecimal.valueOf(90)),
            new Room("104", RoomType.DOUBLE_DELUXE.toString(), 2, BigDecimal.valueOf(90)),
            new Room("105", RoomType.DOUBLE_DELUXE.toString(), 2, BigDecimal.valueOf(90)),
            new Room("106", RoomType.DOUBLE_DELUXE.toString(), 2, BigDecimal.valueOf(90)),
            new Room("107", RoomType.DOUBLE_DELUXE.toString(), 2, BigDecimal.valueOf(90)),
            new Room("201", RoomType.STUDIO_DELUXE.toString(), 3, BigDecimal.valueOf(120)),
            new Room("202", RoomType.STUDIO_DELUXE.toString(), 3, BigDecimal.valueOf(120)),
            new Room("301", RoomType.ONE_BEDROOM_APARTMENT_DELUXE.toString(), 4, BigDecimal.valueOf(150)),
            new Room("302", RoomType.ONE_BEDROOM_APARTMENT_DELUXE.toString(), 4, BigDecimal.valueOf(150)),
            new Room("401", RoomType.TWO_BEDROOM_APARTMENT_DELUXE.toString(), 5, BigDecimal.valueOf(180)),
            new Room("402", RoomType.TWO_BEDROOM_APARTMENT_DELUXE.toString(), 5, BigDecimal.valueOf(180))
            ),
            "Sea View", List.of(
                    new Room("101", RoomType.DOUBLE.toString(), 2, BigDecimal.valueOf(70)),
                    new Room("102", RoomType.DOUBLE.toString(), 2, BigDecimal.valueOf(70)),
                    new Room("103", RoomType.DOUBLE.toString(), 2, BigDecimal.valueOf(70)),
                    new Room("104", RoomType.DOUBLE.toString(), 2, BigDecimal.valueOf(70)),
                    new Room("105", RoomType.DOUBLE.toString(), 2, BigDecimal.valueOf(70)),
                    new Room("106", RoomType.DOUBLE.toString(), 2, BigDecimal.valueOf(70)),
                    new Room("107", RoomType.DOUBLE.toString(), 2, BigDecimal.valueOf(70)),
                    new Room("108", RoomType.DOUBLE.toString(), 2, BigDecimal.valueOf(70)),
                    new Room("201", RoomType.STUDIO.toString(), 3, BigDecimal.valueOf(100)),
                    new Room("202", RoomType.STUDIO.toString(), 3, BigDecimal.valueOf(100)),
                    new Room("203", RoomType.STUDIO.toString(), 3, BigDecimal.valueOf(100)),
                    new Room("301", RoomType.ONE_BEDROOM_APARTMENT.toString(), 4, BigDecimal.valueOf(120)),
                    new Room("302", RoomType.ONE_BEDROOM_APARTMENT.toString(), 4, BigDecimal.valueOf(120)),
                    new Room("303", RoomType.ONE_BEDROOM_APARTMENT.toString(), 4, BigDecimal.valueOf(120)),
                    new Room("401", RoomType.TWO_BEDROOM_APARTMENT.toString(), 5, BigDecimal.valueOf(150)),
                    new Room("402", RoomType.TWO_BEDROOM_APARTMENT.toString(), 5, BigDecimal.valueOf(150)),
                    new Room("403", RoomType.TWO_BEDROOM_APARTMENT.toString(), 5, BigDecimal.valueOf(150))
            ));

    private static final Map<String, List<Event>> SAMPLE_FACILITIES = Map.of("Congress Hall", List.of(
            new Event("Chess tournament", EventType.SPORT.toString(),
                    LocalDateTime.of(2020, 8, 15, 10, 30),
                    LocalDateTime.of(2020, 8, 20, 12, 30))
    ), "Multifunctional Sport Hall", List.of(
            new Event("Volleyball tournament", EventType.SPORT.toString(),
                    LocalDateTime.of(2020, 8, 20, 10, 30),
                    LocalDateTime.of(2020, 8, 24, 15, 30)
            )), "Seminar room", List.of(
            new Event("Tourism Congress", EventType.CONFERENCE.toString(),
                    LocalDateTime.of(2020, 9, 20, 10, 30),
                    LocalDateTime.of(2020, 9, 24, 15, 30)
            )));

    @Override
    public void run(String... args) throws Exception {
        if (this.categoryService.getAllCategories().size() == 0) {
            this.categoryService.saveCategoriesInDatabase();
        }

        if (this.roleService.getAllAuthorities().size() == 0) {
            this.roleService.saveRoleTypesInDatabase();

        }

        if (this.barService.getAllBars().size() == 0) {
            this.barService.createAndSaveDefault();
        }

        if (this.facilityService.getAllFacilities().size() == 0) {
            SAMPLE_FACILITIES.forEach((facility, events) -> {
                Facility newFacility = Facility.create(facility, events);
                facilityService.createAndSaveDefault(newFacility);
            });
        }

        if (this.hotelService.getAllHotels().size() == 0) {
            SAMPLE_HOTELS.forEach((hotel, rooms) -> {
                Hotel newHotel = Hotel.create(hotel, rooms);
                hotelService.createHotel(newHotel);
            });
        }


        if (this.restaurantService.getAllRestaurants().size() == 0) {
            this.restaurantService.createAndSaveDefault();
        }

        if (this.sportActivityService.getAllSportActivities().size() == 0) {
            this.sportActivityService.createAndSaveDefault();
        }

    }
}
