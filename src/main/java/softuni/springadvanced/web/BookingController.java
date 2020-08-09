package softuni.springadvanced.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @GetMapping("/booking-summary")
    @PageTitle("Booking summary")
    public String bookingSummary(Model model){
        return "booking-summary";
    }

    @GetMapping("/booking-no-availability")
    @PageTitle("No available rooms")
    public String bookingNoAvailability(Model model){
        return "booking-no-availability";
    }

    @GetMapping("/booking-not-in-working-hours")
    @PageTitle("Outside of working hours")
    public String bookingHourNotValid(Model model){
        return "booking-not-in-working-hours";
    }
}
