package softuni.springadvanced.services;

import softuni.springadvanced.models.entity.Event;

import java.util.List;

public interface EventService {

    void saveEventsInDatabase();

    Event getEventByName(String eventName);

    List<Event> getAllEvents();

    List<String> getAllEventNames();
}
