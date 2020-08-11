package softuni.springadvanced.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.binding.BookingAddBindingModel;
import softuni.springadvanced.models.entity.Booking;
import softuni.springadvanced.models.entity.BookingType;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.repositories.BookingRepository;
import softuni.springadvanced.services.BookingService;
import softuni.springadvanced.services.UserService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ModelMapper modelMapper) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void saveBookingInDatabase(BookingServiceModel bookingServiceModel) {
        Booking booking = this.modelMapper.map(bookingServiceModel, Booking.class);
        User user = this.userService.getUserByLastname(bookingServiceModel.getUser().getLastName());
        booking.setUser(user);

        this.bookingRepository.saveAndFlush(booking);
    }

    @Override
    public Booking getBookingById(String id) {
        return this.bookingRepository.findById(id).orElse(null);
    }

    @Override
    public Booking getBookingByName(String bookingName) {
        return this.bookingRepository.findByBookingName(bookingName).orElse(null);
    }

    @Override
    public Booking getBookingByUser(User user) {
        return this.bookingRepository.findByUser(user).orElse(null);
    }

    @Override
    public void deleteBooking(Booking booking) {
        this.bookingRepository.delete(booking);
    }

    @Override
    public BookingServiceModel getBookingByPrincipalName(BookingAddBindingModel bookingAddBindingModel, String username, String type) {
        BookingServiceModel bookingServiceModel = this.modelMapper.map(bookingAddBindingModel, BookingServiceModel.class);
        String bookingName = bookingAddBindingModel.getUserLastName() + "-" + bookingAddBindingModel.getFacilityName();
        bookingServiceModel.setBookingName(bookingName);
        bookingServiceModel.setBookingType(type);

        UserServiceModel user = this.modelMapper.
                map(this.userService.getUserByUsername(username), UserServiceModel.class);
        bookingServiceModel.setUser(user);

        BigDecimal price = BigDecimal.ZERO;
        bookingServiceModel.setPrice(price);
        return bookingServiceModel;
    }

    @Override
    public LocalDate getLocalDateByBookingServiceModel(BookingServiceModel bookingServiceModel) {
        int year = bookingServiceModel.getStartDate().getYear();
        int month = bookingServiceModel.getStartDate().getMonthValue();
        int day = bookingServiceModel.getStartDate().getDayOfMonth();
        return LocalDate.of(year, month, day);
    }
}
