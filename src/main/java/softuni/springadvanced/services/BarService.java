package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Bar;

import java.util.List;

public interface BarService {

    void saveBarsInDatabase();

    void createAndSaveDefault();

    Bar getBarByName(String barName);

    List<Bar> getAllBars();
}
