package softuni.springadvanced.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import softuni.springadvanced.models.entity.EventType;
import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.EventService;
import softuni.springadvanced.services.FacilityService;
import softuni.springadvanced.services.UserService;
import softuni.springadvanced.web.annotations.PageTitle;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

@Controller
@RequestMapping("/events")
public class EventController {

    private final BookingService bookingService;
    private final FacilityService facilityService;

    @Autowired
    public EventController(BookingService bookingService, FacilityService facilityService) {
        this.bookingService = bookingService;
        this.facilityService = facilityService;
    }

    @GetMapping("/events-info")
    @PageTitle("Events")
    public String info(@ModelAttribute("bookingAddBindingModel")
                               BookingAddBindingModel bookingAddBindingModel,
                       Model model) {

        List<Integer> nums = List.of(1, 2, 3, 4, 5);
        List<String> eventTypes = List.of(EventType.CONFERENCE.toString(), EventType.SPORT.toString(),
                EventType.WEDDING.toString());

        if (!model.containsAttribute("eventTypes")) {
            model.addAttribute("eventTypes", eventTypes);
            model.addAttribute("nums", nums);
        }

        return "events-info";
    }

    @PostMapping("/events-info")
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView,
                                 RedirectAttributes redirectAttributes, Principal principal) {

        if (bindingResult.hasErrors()) {
            this.bookingService.validateBooking(bookingAddBindingModel);
            modelAndView.setViewName("redirect:events-info");
            return modelAndView;
        }

        if (bookingAddBindingModel.getEndDate() != null) {

            if (bookingAddBindingModel.getStartDate().isEqual(bookingAddBindingModel.getEndDate())
                    || bookingAddBindingModel.getStartDate().isAfter(bookingAddBindingModel.getEndDate())) {

                modelAndView.setViewName("redirect:events-info");
                throw new UsernameNotFoundException("Start date should be before end date!");

            } else {

                BookingServiceModel bookingServiceModel = this.bookingService.getBookingByPrincipalName(bookingAddBindingModel, principal.getName(), BookingType.EVENT.toString());
                LocalDate askedDate = this.bookingService.getLocalDateByBookingServiceModel(bookingServiceModel);
                List<Facility> facilities = this.facilityService.getFacilityByType(bookingAddBindingModel.getFacilityName());

                boolean isAvailableCapacity = false;

                int hour = bookingServiceModel.getStartDate().getHour();
                int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

                for (Facility facility : facilities) {

                    if (numberOfGuests > facility.getGuestsCapacity()) {
                        continue;
                    }

                    this.facilityService.putMapToFacility(askedDate, hour, facility);

                    boolean areAvailableSeatsAtHour = this.facilityService.getAvailableSeatsPerDateTime(askedDate, hour, facility, numberOfGuests);

                    if (areAvailableSeatsAtHour) {
                        int seatsAtDefinedHour = facility.getAvailabilityPerDayAndHour()
                                .get(askedDate).get(hour);

                        this.facilityService.setNumberOfSeatsInMap(askedDate, hour, numberOfGuests, facility, seatsAtDefinedHour);

                        bookingServiceModel.setBookingName(bookingAddBindingModel.getUserLastName() + "-" +
                                facility.getFacilityName());
                        this.bookingService.saveBookingInDatabase(bookingServiceModel);
                        isAvailableCapacity = true;
                        modelAndView.setViewName("redirect:/bookings/booking-summary");
                        break;

                    }
                }

                if (!isAvailableCapacity) {
                    modelAndView.setViewName("redirect:/bookings/booking-no-availability");
                }
            }

        }
        return modelAndView;
    }

    private void setNumberOfSeatsInMap(LocalDate askedDate, int hour, int numberOfGuests, Facility facility, int seatsAtDefinedHour) {
        facility.getAvailabilityPerDayAndHour().get(askedDate)
                .put(hour, seatsAtDefinedHour - numberOfGuests);
    }

}
