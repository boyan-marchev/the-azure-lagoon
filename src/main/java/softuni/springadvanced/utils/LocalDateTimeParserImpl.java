package softuni.springadvanced.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LocalDateTimeParserImpl implements LocalDateTimeParser {
    @Override
    public LocalDateTime parseFromString(String toConvertFrom) {
        String[] dateTime = toConvertFrom.split("\\s+");

        String[] date = dateTime[0].split("\\-");
        String[] time = dateTime[1].split("\\:");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);

        int hour = Integer.parseInt(time[0]);
        int minutes = Integer.parseInt(time[1]);
//        int seconds = Integer.parseInt(time[2]);

        return LocalDateTime.of(year, month, day, hour, minutes);
    }
}
