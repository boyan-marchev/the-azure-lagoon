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
import softuni.springadvanced.models.entity.EventType;
import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.EventService;
import softuni.springadvanced.services.FacilityService;
import softuni.springadvanced.services.UserService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final BookingService bookingService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final FacilityService facilityService;

    @Autowired
    public EventController(EventService eventService, BookingService bookingService, ModelMapper modelMapper, UserService userService, FacilityService facilityService) {
        this.eventService = eventService;
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.facilityService = facilityService;
    }

    @GetMapping("/events-info")
    public String info(@ModelAttribute("bookingAddBindingModel")
                               BookingAddBindingModel bookingAddBindingModel,
                       Model model){

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
            addFlashAttributes(bookingAddBindingModel, bindingResult, modelAndView, redirectAttributes);

        } else {

            // TODO: 07-Aug-20 create logic for event request
            BookingServiceModel bookingServiceModel = this.modelMapper.map(bookingAddBindingModel, BookingServiceModel.class);

            int year = bookingServiceModel.getStartDate().getYear();
            int month = bookingServiceModel.getStartDate().getMonthValue();
            int day = bookingServiceModel.getStartDate().getDayOfMonth();
            LocalDate askedDate = LocalDate.of(year, month, day);

            int hour = bookingServiceModel.getStartDate().getHour();

            String bookingName = bookingAddBindingModel.getUserLastName() + "-" + bookingAddBindingModel.getFacilityName();

            String username = principal.getName();

            UserServiceModel user = this.modelMapper.map(this.userService.getUserByUsername(username),
                    UserServiceModel.class);

            String bookingType = BookingType.EVENT.toString();

            String facilityName = bookingAddBindingModel.getFacilityName();

            int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

            bookingServiceModel.setBookingName(bookingName);
            bookingServiceModel.setBookingType(bookingType);
            bookingServiceModel.setUser(user);

            BigDecimal price = BigDecimal.ZERO;

            bookingServiceModel.setPrice(price);

            Facility facility = this.facilityService.getFacilityByName(facilityName);

            if (facility.getAvailabilityPerDayAndHour() == null || facility.getAvailabilityPerDayAndHour().isEmpty()){
                if (facility.getAvailabilityPerDayAndHour() != null) {
                    facility.getAvailabilityPerDayAndHour().put(askedDate, new TreeMap<>());
                }
                facility.getAvailabilityPerDayAndHour().get(askedDate)
                        .put(hour, facility.getGuestsCapacity());
            }


            boolean areAvailableSeatsAtHour = this.getAvailableSeatsPerDateTime(askedDate, hour, facility);

            if (areAvailableSeatsAtHour){
                int seatsAtDefinedHour = facility.getAvailabilityPerDayAndHour()
                        .get(askedDate).get(hour);

                facility.getAvailabilityPerDayAndHour().get(askedDate)
                        .put(hour, seatsAtDefinedHour - numberOfGuests);

                this.bookingService.saveBookingInDatabase(bookingServiceModel);
                modelAndView.setViewName("redirect:events-booking"); // TODO: 07-Aug-20 add html

            } else {
                modelAndView.setViewName("redirect:events-info");

            }

        }

        return modelAndView;
    }

    private boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Facility facility) {

        return facility.getAvailabilityPerDayAndHour() == null ||
                facility.getAvailabilityPerDayAndHour().get(askedDate).get(hour) > 0
                || facility.getAvailabilityPerDayAndHour().isEmpty();
    }

    private void addFlashAttributes(BookingAddBindingModel bookingAddBindingModel, BindingResult bindingResult, ModelAndView modelAndView, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bookingAddBindingModel", bookingAddBindingModel);
        redirectAttributes
                .addFlashAttribute("org.springframework.validation.BindingResult.homeworkAddBindingModel"
                        , bindingResult);
        modelAndView.setViewName("redirect:events-info");
    }
}
