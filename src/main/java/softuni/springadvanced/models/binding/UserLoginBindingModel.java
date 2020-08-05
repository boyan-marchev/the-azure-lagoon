package softuni.springadvanced.models.binding;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class UserLoginBindingModel {

    @NotNull(message = "")
    @Length(min = 3, message = "Username must be more than 2 characters")
    private String username;

    @NotNull(message = "")
    @Length(min = 3, message = "Password must be more than 2 characters")
    private String password;
}
