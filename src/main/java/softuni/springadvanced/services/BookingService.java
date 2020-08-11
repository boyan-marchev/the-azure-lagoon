package softuni.springadvanced.services;

import softuni.springadvanced.models.binding.BookingAddBindingModel;
import softuni.springadvanced.models.entity.Booking;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.BookingServiceModel;

import java.time.LocalDate;

public interface BookingService {

    void saveBookingInDatabase(BookingServiceModel bookingServiceModel);

    Booking getBookingById(String id);

    Booking getBookingByName(String bookingName);

    Booking getBookingByUser(User user);

    void deleteBooking(Booking booking);

    BookingServiceModel getBookingByPrincipalName(BookingAddBindingModel bookingAddBindingModel, String username, String type);

    LocalDate getLocalDateByBookingServiceModel(BookingServiceModel bookingServiceModel);
}
