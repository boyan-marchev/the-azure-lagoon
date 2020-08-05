package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Bar;
import softuni.springadvanced.repositories.BarRepository;
import softuni.springadvanced.services.BarService;

import java.util.List;

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

    private void createBar(String name, int capacity, int availableSeats) {
        Bar bar = new Bar();
        bar.setBarName(name);
        bar.setSeatsCapacity(capacity);
        bar.setAvailableSeats(availableSeats);

        this.barRepository.save(bar);
    }
}
