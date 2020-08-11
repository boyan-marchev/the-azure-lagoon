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
import softuni.springadvanced.models.entity.Room;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.HotelService;
import softuni.springadvanced.services.RoomService;
import softuni.springadvanced.web.annotations.PageTitle;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;
    private final ModelMapper modelMapper;
    private final RoomService roomService;

    @Autowired
    public HotelController(HotelService hotelService, BookingService bookingService, ModelMapper modelMapper, RoomService roomService) {
        this.hotelService = hotelService;
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
        this.roomService = roomService;
    }

    @GetMapping("/hotels-info")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Hotels")
    public String info(@ModelAttribute("bookingAddBindingModel") BookingAddBindingModel bookingAddBindingModel,
                       Model model) {
        List<String> hotelsByName = this.hotelService.allHotelsByName();
        List<Integer> nums = List.of(1, 2, 3, 4, 5);

        if (!model.containsAttribute("hotelsByName")) {
            model.addAttribute("hotelsByName", hotelsByName);
            model.addAttribute("nums", nums);
        }

        return "hotels-info";
    }

    @PostMapping("/hotels-info")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView, Principal principal) {

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("redirect:hotels-info");
            return modelAndView;

        }

        if (bookingAddBindingModel.getEndDate() != null) {

            if (bookingAddBindingModel.getStartDate().isEqual(bookingAddBindingModel.getEndDate())
                    || bookingAddBindingModel.getStartDate().isAfter(bookingAddBindingModel.getEndDate())) {

                modelAndView.setViewName("redirect:hotels-info");

            } else {

                BookingServiceModel bookingServiceModel = this.bookingService.getBookingByPrincipalName(bookingAddBindingModel, principal.getName(), BookingType.HOTEL.toString());

                int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

                List<Room> availableRooms = this.hotelService.getRoomsByHotelNameAndGuestsNumber(bookingServiceModel.getFacilityName(), numberOfGuests);

                BigDecimal price = BigDecimal.ZERO;
                Room availableRoom = this.roomService.getAvailableRoomByDates(availableRooms, bookingServiceModel.getStartDate(), bookingServiceModel.getEndDate(),
                        bookingServiceModel.getBookingName());

                long overnights = this.hotelService.getOvernights(bookingServiceModel.getStartDate(), bookingServiceModel.getEndDate());

                if (availableRoom != null && overnights >= 1) {
                    price = availableRoom.getPricePerNight().multiply(BigDecimal.valueOf(overnights));
                    bookingServiceModel.setPrice(price);

                    this.bookingService.saveBookingInDatabase(bookingServiceModel);
                    modelAndView.setViewName("redirect:/bookings/booking-summary");

                } else {
                    modelAndView.setViewName("redirect:/bookings/booking-no-availability");

                }

            }

        }
        return modelAndView;
    }

    @GetMapping("/hotels-booking")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Booking")
    public String hotelsBooking () {
        return "booking-summary";
    }
}
