package softuni.springadvanced.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Event;
import softuni.springadvanced.models.entity.EventType;
import softuni.springadvanced.models.entity.Facility;
import softuni.springadvanced.repositories.EventRepository;
import softuni.springadvanced.services.EventService;
import softuni.springadvanced.services.FacilityService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void saveEventsInDatabase() {
//      admin
    }

    @Override
    public Event getEventByName(String eventName) {
        return this.eventRepository.findByEventName(eventName).orElse(null);
    }

    @Override
    public List<Event> getAllEvents() {
        return this.eventRepository.findAll();
    }

}