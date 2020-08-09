package softuni.springadvanced.models.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 20, message = "Name must be between 3 and 20 characters!")
    private String firstName;

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 20, message = "Name must be between 3 and 20 characters!")
    private String lastName;

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters!")
    private String username;

    @Column(nullable = false)
    @Length(min = 3, message = "Password must be between 3 and 20 characters!")
    private String password;

    @Email(message = "Enter valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @DecimalMin(value = "0")
    private BigDecimal budget;


    @ToString.Exclude
    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            )
    )
    private Set<Role> authorities;


    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }
}
