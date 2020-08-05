package softuni.springadvanced.models.service;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryServiceModel extends BaseServiceModel {

    //    @Enumerated(EnumType.STRING)
    @NotNull(message = "")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
