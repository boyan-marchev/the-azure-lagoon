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
import softuni.springadvanced.models.entity.Restaurant;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.BarService;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.UserService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

@Controller
@RequestMapping("/bars")
public class BarController {

    private final BarService barService;
    private final BookingService bookingService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Autowired
    public BarController(BarService barService, BookingService bookingService, ModelMapper modelMapper, UserService userService) {
        this.barService = barService;
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @GetMapping("/bars-info")
    @PageTitle("Bars")
    public String info(@ModelAttribute("bookingAddBindingModel")
                                   BookingAddBindingModel bookingAddBindingModel,
                       Model model){

        List<Integer> nums = List.of(1, 2, 3, 4, 5);
        List<String> barNames = this.barService.getAllBarNames();

        if (!model.containsAttribute("barNames")) {
            model.addAttribute("barNames", barNames);
            model.addAttribute("nums", nums);
        }

        return "bars-info";
    }

    @PostMapping("/bars-info")
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView,
                                 RedirectAttributes redirectAttributes, Principal principal) {

        if (bindingResult.hasErrors()) {
            addFlashAttributes(bookingAddBindingModel, bindingResult, modelAndView, redirectAttributes);

        } else {

            BookingServiceModel bookingServiceModel = this.modelMapper.map(bookingAddBindingModel, BookingServiceModel.class);

            int year = bookingServiceModel.getStartDate().getYear();
            int month = bookingServiceModel.getStartDate().getMonthValue();
            int day = bookingServiceModel.getStartDate().getDayOfMonth();

            int hour = bookingServiceModel.getStartDate().getHour();

            LocalDate askedDate = LocalDate.of(year, month, day);


            if (hour < 10 || hour > 22) {
                modelAndView.setViewName("redirect:/bookings/booking-not-in-working-hours");
                return modelAndView;

            }

            String bookingName = bookingAddBindingModel.getUserLastName() + "-" + bookingAddBindingModel.getFacilityName();

            String username = principal.getName();

            UserServiceModel user = this.modelMapper.map(this.userService.getUserByUsername(username),
                    UserServiceModel.class);

            String bookingType = BookingType.BAR.toString();

            String barName = bookingAddBindingModel.getFacilityName();

            int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

            bookingServiceModel.setBookingName(bookingName);
            bookingServiceModel.setBookingType(bookingType);
            bookingServiceModel.setUser(user);

            BigDecimal price = BigDecimal.ZERO;

            bookingServiceModel.setPrice(price);

            Bar bar = this.barService.getBarByName(barName);

            if (bar.getAvailableSeatsPerDayAndHour() == null || bar.getAvailableSeatsPerDayAndHour().isEmpty()){
                if (bar.getAvailableSeatsPerDayAndHour() != null) {
                    bar.getAvailableSeatsPerDayAndHour().put(askedDate, new TreeMap<>());
                }
                bar.getAvailableSeatsPerDayAndHour().get(askedDate)
                        .put(hour, bar.getAvailableSeats());
            }


            boolean areAvailableSeatsAtHour = this.getAvailableSeatsPerDateTime(askedDate, hour, bar);

            if (areAvailableSeatsAtHour){
                int seatsAtDefinedHour = bar.getAvailableSeatsPerDayAndHour()
                        .get(askedDate).get(hour);

                bar.getAvailableSeatsPerDayAndHour().get(askedDate)
                        .put(hour, seatsAtDefinedHour - numberOfGuests);

                this.bookingService.saveBookingInDatabase(bookingServiceModel);
                modelAndView.setViewName("redirect:bars-booking"); // TODO: 07-Aug-20 add html

            } else {
                modelAndView.setViewName("redirect:bars-info");

            }

        }

        return modelAndView;
    }

    private void addFlashAttributes(BookingAddBindingModel bookingAddBindingModel, BindingResult bindingResult, ModelAndView modelAndView, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bookingAddBindingModel", bookingAddBindingModel);
        redirectAttributes
                .addFlashAttribute("org.springframework.validation.BindingResult.homeworkAddBindingModel"
                        , bindingResult);
        modelAndView.setViewName("redirect:bars-info");
    }

    private boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Bar bar) {

        return bar.getAvailableSeatsPerDayAndHour() == null ||
                bar.getAvailableSeatsPerDayAndHour().get(askedDate).get(hour) > 0
                || bar.getAvailableSeatsPerDayAndHour().isEmpty();
    }
}
