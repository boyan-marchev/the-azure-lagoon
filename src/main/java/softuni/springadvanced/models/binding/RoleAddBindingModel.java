package softuni.springadvanced.models.binding;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleAddBindingModel {

    @NotNull(message = "")
    @Size(min = 2, max = 10, message = "Username must be between 2 and 10 characters")
    private String username;

    @NotNull(message = "")
    private String authority;
}
