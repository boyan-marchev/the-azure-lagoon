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
import softuni.springadvanced.models.entity.Bar;
import softuni.springadvanced.models.entity.BookingType;
import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.FacilityService;
import softuni.springadvanced.services.SportActivityService;
import softuni.springadvanced.services.UserService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

@Controller
@RequestMapping("/sport-activities")
public class SportActivityController {

    private final SportActivityService sportActivityService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final BookingService bookingService;
    private final FacilityService facilityService;

    @Autowired
    public SportActivityController(SportActivityService sportActivityService, ModelMapper modelMapper, UserService userService, BookingService bookingService, FacilityService facilityService) {
        this.sportActivityService = sportActivityService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.bookingService = bookingService;
        this.facilityService = facilityService;
    }

    @GetMapping("/sport-info")
    @PageTitle("Sport activities")
    public String info(@ModelAttribute("bookingAddBindingModel")
                               BookingAddBindingModel bookingAddBindingModel,
                       Model model){

        List<Integer> nums = List.of(1, 2, 3, 4, 5);
        List<String> facilityNames = this.facilityService.getSportFacilitiesNames();
        List<String> sportActivitiesAsString = this.sportActivityService.getSportActivityNames();

        if (!model.containsAttribute("facilityNames")) {
            model.addAttribute("sportActivitiesAsString", sportActivitiesAsString);
            model.addAttribute("facilityNames", facilityNames);
            model.addAttribute("nums", nums);
        }

        return "sport-info";
    }

    @PostMapping("/sport-info")
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView,
                                 RedirectAttributes redirectAttributes, Principal principal) {

        if (bindingResult.hasErrors()) {
            addFlashAttributes(bookingAddBindingModel, bindingResult, modelAndView, redirectAttributes);

        } else {

            BookingServiceModel bookingServiceModel = this.modelMapper.map(bookingAddBindingModel, BookingServiceModel.class);

            LocalDate askedDate = createLocalDate(bookingServiceModel);

            int hour = bookingServiceModel.getStartDate().getHour();

            if (hour < 8 || hour > 20) {
                modelAndView.setViewName("redirect:/bookings/booking-not-in-working-hours");
                return modelAndView;

            }

            String bookingName = bookingAddBindingModel.getUserLastName() + "-" + bookingAddBindingModel.getFacilityName();

            allocateUserServiceModelToBookingServiceModel(principal, bookingServiceModel);

            String bookingType = BookingType.SPORT.toString();

            String facilityName = bookingAddBindingModel.getFacilityName();

            BigDecimal price = BigDecimal.ZERO;

            int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

            setPropertiesToBookingServiceModel(bookingServiceModel, bookingName, bookingType, price);

            Facility facility = this.facilityService.getFacilityByName(facilityName);

            saveBooking(modelAndView, bookingServiceModel, askedDate, hour, numberOfGuests, facility);

        }

        return modelAndView;
    }

    private void saveBooking(ModelAndView modelAndView, BookingServiceModel bookingServiceModel, LocalDate askedDate, int hour, int numberOfGuests, Facility facility) {
        if (facility.getAvailabilityPerDayAndHour() == null || facility.getAvailabilityPerDayAndHour().isEmpty()){
            if (facility.getAvailabilityPerDayAndHour() != null) {
                facility.getAvailabilityPerDayAndHour().put(askedDate, new TreeMap<>());
            }
            if (facility.getAvailabilityPerDayAndHour() != null) {
                facility.getAvailabilityPerDayAndHour().get(askedDate)
                        .put(hour, facility.getGuestsCapacity());
            }
        }


        boolean areAvailableSeatsAtHour = this.getAvailableSeatsPerDateTime(askedDate, hour, facility);

        if (areAvailableSeatsAtHour){
            int seatsAtDefinedHour = facility.getAvailabilityPerDayAndHour()
                    .get(askedDate).get(hour);

            facility.getAvailabilityPerDayAndHour().get(askedDate)
                    .put(hour, seatsAtDefinedHour - numberOfGuests);

            this.bookingService.saveBookingInDatabase(bookingServiceModel);
            modelAndView.setViewName("redirect:/bookings/booking-summary");

        } else {
            modelAndView.setViewName("redirect:/bookings/booking-no-availability");

        }
    }

    private void setPropertiesToBookingServiceModel(BookingServiceModel bookingServiceModel, String bookingName, String bookingType, BigDecimal price) {
        bookingServiceModel.setBookingName(bookingName);
        bookingServiceModel.setBookingType(bookingType);
        bookingServiceModel.setPrice(price);
    }

    private void allocateUserServiceModelToBookingServiceModel(Principal principal, BookingServiceModel bookingServiceModel) {
        String username = principal.getName();

        UserServiceModel user = this.modelMapper.map(this.userService.getUserByUsername(username),
                UserServiceModel.class);

        bookingServiceModel.setUser(user);
    }


    private LocalDate createLocalDate(BookingServiceModel bookingServiceModel) {
        int year = bookingServiceModel.getStartDate().getYear();
        int month = bookingServiceModel.getStartDate().getMonthValue();
        int day = bookingServiceModel.getStartDate().getDayOfMonth();
        return LocalDate.of(year, month, day);
    }

    private void addFlashAttributes(BookingAddBindingModel bookingAddBindingModel, BindingResult bindingResult, ModelAndView modelAndView, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bookingAddBindingModel", bookingAddBindingModel);
        redirectAttributes
                .addFlashAttribute("org.springframework.validation.BindingResult.homeworkAddBindingModel"
                        , bindingResult);
        modelAndView.setViewName("redirect:sport-info");
    }

    private boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Facility facility) {

        return facility.getAvailabilityPerDayAndHour() == null ||
                facility.getAvailabilityPerDayAndHour().get(askedDate).get(hour) > 0
                || facility.getAvailabilityPerDayAndHour().isEmpty();
    }

}
