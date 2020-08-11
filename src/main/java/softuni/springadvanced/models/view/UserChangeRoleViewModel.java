package softuni.springadvanced.models.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.springadvanced.models.service.RoleServiceModel;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class UserChangeRoleViewModel {

//    private String firstName;
//
//    private String lastName;

    private String username;

    private Set<String> authorities;
}
