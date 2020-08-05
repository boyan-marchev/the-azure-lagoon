package softuni.springadvanced.models.service;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleServiceModel extends BaseServiceModel {

    @NotNull
    private String authority;

}
