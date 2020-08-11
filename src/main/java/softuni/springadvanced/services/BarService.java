package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Bar;
import softuni.springadvanced.models.service.BookingServiceModel;

import java.time.LocalDate;
import java.util.List;

public interface BarService {

    void saveBarsInDatabase();

    void createAndSaveDefault();

    Bar getBarByName(String barName);

    List<Bar> getAllBars();

    List<String> getAllBarNames();

    void putMapToBar(BookingServiceModel bookingServiceModel, Bar bar, LocalDate askedDate);

    boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Bar bar);
}
