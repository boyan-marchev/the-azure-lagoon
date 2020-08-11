package softuni.springadvanced.web.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import softuni.springadvanced.web.annotations.PageTitle;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @GetMapping("/booking-summary")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Booking summary")
    public String bookingSummary(Model model){
        return "booking-summary";
    }

    @GetMapping("/booking-no-availability")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("No available rooms")
    public String bookingNoAvailability(Model model){
        return "booking-no-availability";
    }

    @GetMapping("/booking-not-in-working-hours")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Outside of working hours")
    public String bookingHourNotValid(Model model){
        return "booking-not-in-working-hours";
    }
}
