package softuni.springadvanced.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.binding.UserLoginBindingModel;
import softuni.springadvanced.models.binding.UserRegisterBindingModel;
import softuni.springadvanced.models.entity.MemberStatus;
import softuni.springadvanced.models.entity.Role;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.models.view.UserChangeRoleViewModel;
import softuni.springadvanced.repositories.UserRepository;
import softuni.springadvanced.services.RoleService;
import softuni.springadvanced.services.UserService;


import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, @Lazy RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();

    }


    @Override
    public User getUserByLastname(String lastName) {
        return this.userRepository.findByLastName(lastName)
                .orElseThrow(() -> new UsernameNotFoundException("No such user found"));

    }

    @Override
    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    public UserServiceModel getUserServiceModelById(String id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No such user found"));

        return this.modelMapper.map(user, UserServiceModel.class);


    }

    @Override
    public void saveUserInDatabase(UserServiceModel userServiceModel) {
        if (this.getAllUsers().size() == 0) {
            userServiceModel.setAuthorities(this.roleService.getAllAuthoritiesAsServiceModels());

        } else {
            userServiceModel.setAuthorities(new LinkedHashSet<>());
            userServiceModel.getAuthorities().add(this.modelMapper.map(this.roleService.getRoleByAuthority("ROLE_USER"),
                    RoleServiceModel.class));
        }

        userServiceModel.setPassword(bCryptPasswordEncoder.encode(userServiceModel.getPassword()));
        userServiceModel.setBudget(BigDecimal.ZERO);
        userServiceModel.setMemberStatus(MemberStatus.ROOKIE.toString());
        userServiceModel.setClubMemberPoints(0);

        User user = this.modelMapper.map(userServiceModel, User.class);
        this.userRepository.saveAndFlush(user);
    }

    @Override
    public void changeRoleOfUser(String username) {

        User user = this.getUserByUsername(username);

        if (user.getAuthorities().size() == 2) {
            user.getAuthorities().clear();

            Role role = this.roleService.getRoleByAuthority("ROLE_USER");

            user.getAuthorities().add(role);
            this.userRepository.save(user);

        } else {
            user.setAuthorities(this.roleService.getAllAuthorities());

        }

        this.userRepository.save(user);

    }

    @Override
    public List<String> getAllUsernamesAsString() {
        List<User> users = this.getAllUsers();
        List<String> result = new ArrayList<>();

        for (User user : users) {
            result.add(user.getUsername());
        }

        return result;
    }

    @Override
    public List<UserServiceModel> getAllUsersAsServiceModels() {
        return this.getAllUsers().stream()
                .map(user -> this.modelMapper.map(user, UserServiceModel.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<UserChangeRoleViewModel> getAllUsersAsViewChangeRoleModels() {
        List<UserChangeRoleViewModel> result = new ArrayList<>();

        List<UserServiceModel> all = this.getAllUsersAsServiceModels();
        for (UserServiceModel userServiceModel : all) {
            Set<String> toUpdate = new HashSet<>();

            for (RoleServiceModel authority : userServiceModel.getAuthorities()) {
                toUpdate.add(authority.getAuthority());
            }
            UserChangeRoleViewModel userView = this.modelMapper.map(userServiceModel, UserChangeRoleViewModel.class);
            userView.setAuthorities(toUpdate);
            result.add(userView);
        }

        return result;
    }

    @Override
    public void addMemberPointsToUsersInDb() {
        List<User> allUsers = this.getAllUsers();
        for (User user : allUsers) {
            user.setClubMemberPoints(user.getClubMemberPoints() + 3);
        }

        this.userRepository.saveAll(allUsers);
    }

    @Override
    public void checkMemberStatusOfUsers() {
        List<User> allUsers = this.getAllUsers();
        for (User user : allUsers) {
            if (user.getClubMemberPoints() > 100 && user.getMemberStatus().equals(MemberStatus.ROOKIE.toString())) {
                user.setMemberStatus(MemberStatus.SILVER.toString());

            } else if (user.getClubMemberPoints() > 250 && user.getMemberStatus().equals(MemberStatus.SILVER.toString())) {
                user.setMemberStatus(MemberStatus.GOLDEN.toString());

            } else if (user.getClubMemberPoints() > 365 && user.getMemberStatus().equals(MemberStatus.GOLDEN.toString())) {
                user.setMemberStatus(MemberStatus.SENATOR.toString());
            }
        }

        this.userRepository.saveAll(allUsers);
    }

    @Override
    public void deleteUserById(String id) {
        if (this.userRepository.existsById(id)) {
            this.userRepository.deleteById(id);
        }
    }

    @Override
    public User getUserById(String id) {
        return this.userRepository.findById(id).orElse(null);
    }

    @Override
    public void validateUserRegisterBindingModel(UserRegisterBindingModel userRegisterBindingModel) {
        String firstName = userRegisterBindingModel.getFirstName();
        String lastName = userRegisterBindingModel.getLastName();
        String username = userRegisterBindingModel.getUsername();
        String email = userRegisterBindingModel.getEmail();
        String password = userRegisterBindingModel.getPassword();

        boolean isFirstNameValid = true;
        boolean isLastNameValid = true;
        boolean isUsernameValid = true;
        boolean isEmailValid = true;
        boolean isPasswordValid = true;

        StringBuilder builder = new StringBuilder();

        if (firstName == null || firstName.isEmpty() || firstName.length() < 4 || firstName.length() > 20){
            isFirstNameValid = false;
            builder.append("First name must be between 3 and 20 characters!").append(System.lineSeparator());
        }


        if (lastName == null || lastName.isEmpty() || lastName.length() < 4 || lastName.length() > 20){
            isLastNameValid = false;
            builder.append("Last name must be between 3 and 20 characters!").append(System.lineSeparator());
        }


        if (username == null || username.isEmpty() || username.length() < 4 || username.length() > 20){
            isUsernameValid = false;
            builder.append("Username must be between 3 and 20 characters!").append(System.lineSeparator());
        }


        if (email == null || email.isEmpty()){
            isEmailValid = false;
            builder.append("Email should not be empty!").append(System.lineSeparator());
        }

        if (password == null || password.isEmpty() || password.length() < 4 || password.length() > 20){
            isPasswordValid = false;
            builder.append("Password must be between 3 and 20 characters!").append(System.lineSeparator());
        }


        if (!isFirstNameValid || !isLastNameValid || !isUsernameValid || !isEmailValid || !isPasswordValid){
            throw new UsernameNotFoundException(builder.toString());
        }

    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(s).
                orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

}
