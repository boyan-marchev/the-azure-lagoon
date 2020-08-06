package softuni.springadvanced.models.service;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import softuni.springadvanced.models.entity.Role;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserServiceModel extends BaseServiceModel {

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 20, message = "Name must be between 3 and 20 characters!")
    private String firstName;

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 20, message = "Name must be between 3 and 20 characters!")
    private String lastName;

    @NotNull(message = "Username is required field")
    @Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters!")
    private String username;

    @NotNull(message = "Password is required field")
    @Length(min = 3, max = 20, message = "Password must be between 3 and 20 characters!")
    private String password;

    @Email(message = "Enter valid email address")
    @NotNull(message = "Email cannot be empty!")
    private String email;

    @DecimalMin(value = "0")
    private BigDecimal budget;

    private Set<RoleServiceModel> authorities;

    private List<BookingServiceModel> bookings = new ArrayList<>();
}
