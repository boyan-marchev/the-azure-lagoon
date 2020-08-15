package softuni.springadvanced.services;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import softuni.springadvanced.models.binding.UserLoginBindingModel;
import softuni.springadvanced.models.binding.UserRegisterBindingModel;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.models.view.UserChangeRoleViewModel;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> getAllUsers();

    User getUserByLastname(String lastName);

    User getUserByUsername(String username);

    UserServiceModel getUserServiceModelById(String id);

    void saveUserInDatabase(UserServiceModel userServiceModel);

    void changeRoleOfUser(String username);

    List<String> getAllUsernamesAsString();

    List<UserServiceModel> getAllUsersAsServiceModels();

    List<UserChangeRoleViewModel> getAllUsersAsViewChangeRoleModels();

    void addMemberPointsToUsersInDb();

    void checkMemberStatusOfUsers();

    void deleteUserById(String id);

    User getUserById(String id);

    void validateUserRegisterBindingModel(UserRegisterBindingModel userRegisterBindingModel);

}
