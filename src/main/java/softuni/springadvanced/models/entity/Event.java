package softuni.springadvanced.models.entity;

import lombok.*;
import org.apache.tomcat.jni.Local;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
public class Event extends BaseEntity {

    @NotNull
    @Column(unique = true)
    private String eventName;

    @NotNull
    @Column
    private String eventType;

//    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
////    @PastOrPresent(message = "The date cannot be in the future")
//    @FutureOrPresent(message = "The date cannot be in the past")
    @NotNull
    @Column(name = "start_date")
    private LocalDateTime startDate;

//    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//    @FutureOrPresent(message = "The date cannot be in the past")
    @NotNull
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @ToString.Exclude
    private Facility facility;

    public Event(String eventName, String eventType, LocalDateTime startDate, LocalDateTime endDate){
        this.eventName = eventName;
        this.eventType = eventType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
