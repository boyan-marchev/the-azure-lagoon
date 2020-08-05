package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Booking;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.BookingServiceModel;

public interface BookingService {

    void saveBookingInDatabase(BookingServiceModel bookingServiceModel);

    Booking getBookingById(String id);

    Booking getBookingByName(String bookingName);

    Booking getBookingByUser(User user);
}
