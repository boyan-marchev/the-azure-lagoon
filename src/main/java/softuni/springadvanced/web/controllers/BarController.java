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
import softuni.springadvanced.models.entity.Bar;
import softuni.springadvanced.models.entity.BookingType;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.BarService;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.UserService;
import softuni.springadvanced.web.annotations.PageTitle;

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

    @Autowired
    public BarController(BarService barService, BookingService bookingService) {
        this.barService = barService;
        this.bookingService = bookingService;
    }

    @GetMapping("/bars-info")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Bars")
    public String info(@ModelAttribute("bookingAddBindingModel")
                               BookingAddBindingModel bookingAddBindingModel, Model model) {

        List<Integer> nums = List.of(1, 2, 3, 4, 5);
        List<String> barNames = this.barService.getAllBarNames();

        if (!model.containsAttribute("barNames")) {
            model.addAttribute("barNames", barNames);
            model.addAttribute("nums", nums);
        }

        return "bars-info";
    }

    @PostMapping("/bars-info")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView,
                                 Principal principal) {

        if (bindingResult.hasErrors()) {
            this.bookingService.validateBooking(bookingAddBindingModel);
            modelAndView.setViewName("redirect:bars-info");
            return modelAndView;

        } else if (bookingAddBindingModel.getStartDate().getHour() < 10 || bookingAddBindingModel.getStartDate().getHour() > 22) {
            modelAndView.setViewName("redirect:/bookings/booking-not-in-working-hours");
            return modelAndView;

        } else {

            BookingServiceModel bookingServiceModel = this.bookingService.getBookingByPrincipalName(bookingAddBindingModel, principal.getName(), BookingType.BAR.toString());

            LocalDate askedDate = LocalDate.of(bookingServiceModel.getStartDate().getYear(),
                    bookingServiceModel.getStartDate().getMonthValue(), bookingServiceModel.getStartDate().getDayOfMonth());

            int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

            Bar bar = this.barService.getBarByName(bookingAddBindingModel.getFacilityName());

            this.barService.putMapToBar(bookingServiceModel, bar, askedDate);

            boolean areAvailableSeatsAtHour = this.barService.getAvailableSeatsPerDateTime(askedDate, bookingServiceModel.getStartDate().getHour(), bar);

            if (areAvailableSeatsAtHour) {
                int seatsAtDefinedHour = bar.getAvailableSeatsPerDayAndHour()
                        .get(askedDate).get(bookingServiceModel.getStartDate().getHour());

                this.barService.setNumberOfSeatsInMap(bookingServiceModel, askedDate, numberOfGuests, bar, seatsAtDefinedHour);

                this.bookingService.saveBookingInDatabase(bookingServiceModel);
                modelAndView.setViewName("redirect:/bookings/booking-summary");

            } else {
                modelAndView.setViewName("redirect:/bookings/booking-no-availability");

            }

        }

        return modelAndView;
    }

}
