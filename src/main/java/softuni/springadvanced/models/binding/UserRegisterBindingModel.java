package softuni.springadvanced.models.binding;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class UserRegisterBindingModel {

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 20, message = "First name must be between 3 and 20 characters!")
    private String firstName;

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 20, message = "Last name must be between 3 and 20 characters!")
    private String lastName;

    @NotNull(message = "")
    @Length(min = 3, max = 20, message = "Username must be more than 2 characters!")
    private String username;

    @Email(message = "Enter valid email address!")
    @NotNull(message = "Email cannot be empty!")
    private String email;

    @NotNull(message = "")
    @Length(min = 3, message = "Password must be more than 2 characters!")
    private String password;

    @NotNull(message = "")
    @Length(min = 3)
    private String confirmPassword;
}
