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
import softuni.springadvanced.models.entity.Room;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.HotelService;
import softuni.springadvanced.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    @Autowired
    public HotelController(HotelService hotelService, UserService userService, BookingService bookingService, ModelMapper modelMapper) {
        this.hotelService = hotelService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/hotels-info")
    public String info(Model model, HttpSession httpSession) {
        List<String> hotelsByName = this.hotelService.allHotelsByName();
        List<Integer> nums = List.of(1, 2, 3, 4, 5);

        String title = "Hotels";
        if (!model.containsAttribute(title)){
            model.addAttribute("title", title);
        }

        if (!model.containsAttribute("bookingAddBindingModel")) {
            model.addAttribute("bookingAddBindingModel", new BookingAddBindingModel());
        }

        if (!model.containsAttribute("hotelsByName")){
            model.addAttribute("hotelsByName", hotelsByName);
            model.addAttribute("nums", nums);
        }

        return "hotels-info";
    }

    @PostMapping("/hotels-info")
    public ModelAndView infoPost(@Valid @ModelAttribute("bookingAddBindingModel")
                                         BookingAddBindingModel bookingAddBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView,
                                 RedirectAttributes redirectAttributes, Principal principal) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("bookingAddBindingModel", bookingAddBindingModel);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.homeworkAddBindingModel"
                            , bindingResult);
            modelAndView.setViewName("redirect:hotels-info");

        } else {

            BookingServiceModel bookingServiceModel = this.modelMapper.map(bookingAddBindingModel, BookingServiceModel.class);

            if (bookingServiceModel.getEndDate() != null) {
                if (bookingServiceModel.getStartDate().isEqual(bookingServiceModel.getEndDate())
                        || bookingServiceModel.getStartDate().isAfter(bookingServiceModel.getEndDate())) {

                    modelAndView.setViewName("redirect:hotels-info");
                    return modelAndView;
                }
            }

            String bookingName = bookingAddBindingModel.getUserLastName() + "-" + bookingAddBindingModel.getFacilityName();

            String username = principal.getName();

            UserServiceModel user = this.modelMapper.map(this.userService.getUserByUsername(username),
                    UserServiceModel.class);

            String bookingType = BookingType.HOTEL.toString();

            String hotelName = bookingAddBindingModel.getFacilityName();

            int numberOfGuests = bookingAddBindingModel.getNumberOfGuests();

            List<Room> availableRooms = this.hotelService.getRoomsByHotelNameAndGuestsNumber(hotelName, numberOfGuests);

            bookingServiceModel.setBookingName(bookingName);
            bookingServiceModel.setBookingType(bookingType);
            bookingServiceModel.setUser(user);

            BigDecimal price = BigDecimal.ZERO;
            Room availableRoom = availableRoom(availableRooms, bookingServiceModel.getStartDate(), bookingServiceModel.getEndDate(),
                    bookingServiceModel.getBookingName());

            long overnights = getOvernights(bookingServiceModel.getStartDate(), bookingServiceModel.getEndDate());

            if (availableRoom != null && overnights >= 1) {
                price = availableRoom.getPricePerNight().multiply(BigDecimal.valueOf(overnights));
                bookingServiceModel.setPrice(price);

                this.bookingService.saveBookingInDatabase(bookingServiceModel);
                modelAndView.setViewName("redirect:hotels-booking");

            } else {
                modelAndView.setViewName("redirect:hotels-info");

            }

        }


        return modelAndView;
    }

    @GetMapping("/hotels-booking")
    public String hotelsBooking(){
        return "hotels-booking";
    }

    private long getOvernights(LocalDateTime startDate, LocalDateTime endDate) {
        Duration duration = Duration.between(startDate, endDate);
        long days = duration.toDays();

        return days;
    }

    private Room availableRoom(List<Room> availableRooms, LocalDateTime startDate, LocalDateTime endDate,
                               String bookingName) {
        if (availableRooms.size() < 1) {
            return null;
        }

        for (Room room : availableRooms) {
            if (room.getBookedDates().isEmpty()) {
                room.getBookedDates().putIfAbsent(bookingName, new ArrayList<>());
                room.getBookedDates().get(bookingName).add(0, startDate);
                room.getBookedDates().get(bookingName).add(1, endDate);

                return room;

            } else {
                for (Map.Entry<String, List<LocalDateTime>> entry : room.getBookedDates().entrySet()) {
                    List<LocalDateTime> dates = entry.getValue();
                    LocalDateTime start = dates.get(0);
                    LocalDateTime end = dates.get(1);

                    if (startDate.isBefore(start) && endDate.isBefore(start) ||
                            startDate.isAfter(end) || startDate.isAfter(start) && startDate.isAfter(end)) {
                        room.getBookedDates().putIfAbsent(bookingName, new ArrayList<>());
                        room.getBookedDates().get(bookingName).add(0, startDate);
                        room.getBookedDates().get(bookingName).add(1, endDate);

                        return room;

                    }
                }
            }
        }

        return null;
    }
}
