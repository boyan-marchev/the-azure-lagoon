package softuni.springadvanced.web;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.springadvanced.models.binding.BookingAddBindingModel;
import softuni.springadvanced.models.entity.BookingType;
import softuni.springadvanced.models.entity.Restaurant;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.RestaurantService;
import softuni.springadvanced.services.UserService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService, ModelMapper modelMapper, UserService userService, BookingService bookingService) {
        this.restaurantService = restaurantService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/restaurants-info")
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
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView,
                                 RedirectAttributes redirectAttributes, Principal principal) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("bookingAddBindingModel", bookingAddBindingModel);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.homeworkAddBindingModel"
                            , bindingResult);
            modelAndView.setViewName("redirect:restaurants-info");

        } else {

            BookingServiceModel bookingServiceModel = this.modelMapper.map(bookingAddBindingModel, BookingServiceModel.class);

            int year = bookingServiceModel.getStartDate().getYear();
            int month = bookingServiceModel.getStartDate().getMonthValue();
            int day = bookingServiceModel.getStartDate().getDayOfMonth();

            int hour = bookingServiceModel.getStartDate().getHour();

            LocalDate askedDate = LocalDate.of(year, month, day);


            if (hour < 10 || hour > 21) {
                modelAndView.setViewName("redirect:/bookings/booking-not-in-working-hours");
                return modelAndView;

            }

            String bookingName = bookingAddBindingModel.getUserLastName() + "-" + bookingAddBindingModel.getFacilityName();

            String username = principal.getName();

            UserServiceModel user = this.modelMapper.map(this.userService.getUserByUsername(username),
                    UserServiceModel.class);

            String bookingType = BookingType.RESTAURANT.toString();

            String restaurantName = bookingAddBindingModel.getFacilityName();

            Restaurant restaurant = this.restaurantService.getRestaurantByName(restaurantName);

            if (restaurant.getAvailableSeatsPerDayAndHour() == null || restaurant.getAvailableSeatsPerDayAndHour().isEmpty()){
                if (restaurant.getAvailableSeatsPerDayAndHour() != null) {
                    restaurant.getAvailableSeatsPerDayAndHour().put(askedDate, new TreeMap<>());
                }
                restaurant.getAvailableSeatsPerDayAndHour().get(askedDate)
                        .put(hour, restaurant.getAvailableSeats());
            }

            int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

            bookingServiceModel.setBookingName(bookingName);
            bookingServiceModel.setBookingType(bookingType);
            bookingServiceModel.setUser(user);

            BigDecimal price = BigDecimal.ZERO;

            bookingServiceModel.setPrice(price);

            boolean areAvailableSeatsAtHour = this.getAvailableSeatsPerDateTime(askedDate, hour, restaurant);

            if (areAvailableSeatsAtHour){
                int seatsAtDefinedHour = restaurant.getAvailableSeatsPerDayAndHour()
                        .get(askedDate).get(hour);

                restaurant.getAvailableSeatsPerDayAndHour().get(askedDate)
                        .put(hour, seatsAtDefinedHour - numberOfGuests);

                this.bookingService.saveBookingInDatabase(bookingServiceModel);
                modelAndView.setViewName("redirect:/bookings/booking-summary");

            } else {
                modelAndView.setViewName("redirect:/bookings/booking-no-availability");

            }

        }

        return modelAndView;
    }

    @GetMapping("/restaurants-booking")
    @PageTitle("Booking")
    public String restaurantsBooking(Model model) {

        return "restaurants-booking";
    }

    private boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Restaurant restaurant) {

        return restaurant.getAvailableSeatsPerDayAndHour() == null ||
                restaurant.getAvailableSeatsPerDayAndHour().get(askedDate).get(hour) > 0
                || restaurant.getAvailableSeatsPerDayAndHour().isEmpty();
    }

}
