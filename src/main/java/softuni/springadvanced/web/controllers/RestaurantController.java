package softuni.springadvanced.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.springadvanced.models.binding.BookingAddBindingModel;
import softuni.springadvanced.models.entity.BookingType;
import softuni.springadvanced.models.entity.Restaurant;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.RestaurantService;
import softuni.springadvanced.services.UserService;
import softuni.springadvanced.web.annotations.PageTitle;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final BookingService bookingService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService, BookingService bookingService) {
        this.restaurantService = restaurantService;
        this.bookingService = bookingService;
    }

    @GetMapping("/restaurants-info")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Restaurants")
    public String info(@ModelAttribute("bookingAddBindingModel")
                               BookingAddBindingModel bookingAddBindingModel,
                       Model model) {

        List<Integer> nums = List.of(1, 2, 3, 4, 5);
        List<String> restaurantsByName = this.restaurantService.getAllRestaurantsByName();

        if (!model.containsAttribute("restaurantsByName")) {
            model.addAttribute("restaurantsByName", restaurantsByName);
            model.addAttribute("nums", nums);
        }
        return "restaurants-info";
    }

    @PostMapping("/restaurants-info")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView, Principal principal) {

        if (bindingResult.hasErrors()) {
            this.bookingService.validateBooking(bookingAddBindingModel);
            modelAndView.setViewName("redirect:restaurants-info");
            return modelAndView;

        } else if (bookingAddBindingModel.getStartDate().getHour() < 10 || bookingAddBindingModel.getStartDate().getHour() > 21) {
            modelAndView.setViewName("redirect:/bookings/booking-not-in-working-hours");
            return modelAndView;

        } else {
            BookingServiceModel bookingServiceModel = this.bookingService.getBookingByPrincipalName(bookingAddBindingModel, principal.getName(), BookingType.RESTAURANT.toString());

            LocalDate askedDate = LocalDate.of(bookingServiceModel.getStartDate().getYear(),
                    bookingServiceModel.getStartDate().getMonthValue(), bookingServiceModel.getStartDate().getDayOfMonth());

            Restaurant restaurant = this.restaurantService.getRestaurantByName(bookingAddBindingModel.getFacilityName());
            int hour = bookingServiceModel.getStartDate().getHour();

            this.restaurantService.putMapToRestaurantIfAbsent(askedDate, restaurant, hour);

            int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

            boolean areAvailableSeatsAtHour = this.restaurantService.getAvailableSeatsPerDateTime(askedDate, hour, restaurant);

            if (areAvailableSeatsAtHour) {
                int seatsAtDefinedHour = restaurant.getAvailableSeatsPerDayAndHour()
                        .get(askedDate).get(hour);

                this.restaurantService.setNumberOfAvailableRoomsInMap(askedDate, restaurant, hour, numberOfGuests, seatsAtDefinedHour);

                this.bookingService.saveBookingInDatabase(bookingServiceModel);
                modelAndView.setViewName("redirect:/bookings/booking-summary");

            } else {
                modelAndView.setViewName("redirect:/bookings/booking-no-availability");

            }
        }

        return modelAndView;
    }


    @GetMapping("/restaurants-booking")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Booking")
    public String restaurantsBooking(Model model) {

        return "restaurants-booking";
    }

}
