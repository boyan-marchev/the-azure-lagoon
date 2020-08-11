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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.springadvanced.models.binding.BookingAddBindingModel;
import softuni.springadvanced.models.entity.BookingType;
import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.FacilityService;
import softuni.springadvanced.services.SportActivityService;
import softuni.springadvanced.services.UserService;
import softuni.springadvanced.web.annotations.PageTitle;

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
    private final BookingService bookingService;
    private final FacilityService facilityService;

    @Autowired
    public SportActivityController(SportActivityService sportActivityService, BookingService bookingService, FacilityService facilityService) {
        this.sportActivityService = sportActivityService;
        this.bookingService = bookingService;
        this.facilityService = facilityService;
    }

    @GetMapping("/sport-info")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Sport activities")
    public String info(@ModelAttribute("bookingAddBindingModel")
                               BookingAddBindingModel bookingAddBindingModel, Model model) {

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
    @PreAuthorize("isAuthenticated()")
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView,
                                 Principal principal) {

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("redirect:sport-info");

        } else if (bookingAddBindingModel.getStartDate().getHour() < 8 || bookingAddBindingModel.getStartDate().getHour() > 20) {
            modelAndView.setViewName("redirect:/bookings/booking-not-in-working-hours");

        } else {

            BookingServiceModel bookingServiceModel = this.bookingService.getBookingByPrincipalName(bookingAddBindingModel, principal.getName(), BookingType.SPORT.toString());
            LocalDate askedDate = LocalDate.of(bookingServiceModel.getStartDate().getYear(),
                    bookingServiceModel.getStartDate().getMonthValue(), bookingServiceModel.getStartDate().getDayOfMonth());

            int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();
            Facility facility = this.facilityService.getFacilityByName(bookingAddBindingModel.getFacilityName());

            int hour = bookingServiceModel.getStartDate().getHour();
            this.facilityService.putMapToFacility(askedDate, hour, facility);

            boolean areAvailableSeatsAtHour = this.sportActivityService.getAvailableSeatsPerDateTime(askedDate, hour, facility);

            if (areAvailableSeatsAtHour) {
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
        return modelAndView;
    }
}
