package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Bar;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.repositories.BarRepository;
import softuni.springadvanced.services.BarService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
public class BarServiceImpl implements BarService {

    private final BarRepository barRepository;

    @Autowired
    public BarServiceImpl(BarRepository barRepository) {
        this.barRepository = barRepository;
    }

    @Override
    public void saveBarsInDatabase() {
//        admin
    }

    @Override
    public void createAndSaveDefault() {
        createBar("Lounge", 25, 25);
        createBar("Pool", 35, 30);
        createBar("Beach", 35, 35);
    }


    @Override
    public Bar getBarByName(String barName) {
        return this.barRepository.findByBarName(barName).orElse(null);
    }

    @Override
    public List<Bar> getAllBars() {
        return this.barRepository.findAll();
    }

    @Override
    public List<String> getAllBarNames() {
        List<Bar> bars = this.getAllBars();
        List<String> result = new ArrayList<>();

        for (Bar bar : bars) {
            result.add(bar.getBarName());
        }

        return result;
    }

    @Override
    public void putMapToBar(BookingServiceModel bookingServiceModel, Bar bar, LocalDate askedDate) {
        if (bar.getAvailableSeatsPerDayAndHour() == null || bar.getAvailableSeatsPerDayAndHour().isEmpty()){
            if (bar.getAvailableSeatsPerDayAndHour() != null) {
                bar.getAvailableSeatsPerDayAndHour().put(askedDate, new TreeMap<>());
            }
            if (bar.getAvailableSeatsPerDayAndHour() != null) {
                bar.getAvailableSeatsPerDayAndHour().get(askedDate)
                        .put(bookingServiceModel.getStartDate().getHour(), bar.getAvailableSeats());
            }
        }
    }

    @Override
    public boolean getAvailableSeatsPerDateTime(LocalDate askedDate, int hour, Bar bar) {
        return bar.getAvailableSeatsPerDayAndHour() == null ||
                bar.getAvailableSeatsPerDayAndHour().get(askedDate).get(hour) > 0
                || bar.getAvailableSeatsPerDayAndHour().isEmpty();
    }

    private void createBar(String name, int capacity, int availableSeats) {
        Bar bar = new Bar();
        bar.setBarName(name);
        bar.setSeatsCapacity(capacity);
        bar.setAvailableSeats(availableSeats);

        this.barRepository.save(bar);
    }
}
