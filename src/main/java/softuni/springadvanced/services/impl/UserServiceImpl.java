package softuni.springadvanced.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Role;
import softuni.springadvanced.models.entity.Roles;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.models.view.UserChangeRoleViewModel;
import softuni.springadvanced.repositories.UserRepository;
import softuni.springadvanced.services.BookingService;
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
    public UserServiceModel getUserServiceModelByUsername(String username) {

        User user = this.userRepository.findByUsername(username).orElse(null);

        if (user != null) {

            return this.modelMapper.map(user, UserServiceModel.class);

        } else {
            return null;

        }
    }

    @Override
    public UserServiceModel getUserServiceModelByLastname(String lastName) {

        User user = this.userRepository.findByLastName(lastName)
                .orElseThrow(() -> new UsernameNotFoundException("No such user found"));

        return this.modelMapper.map(user, UserServiceModel.class);

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

        User user = this.modelMapper.map(userServiceModel, User.class);
        user.setBudget(BigDecimal.ZERO);
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
    public void deleteUserById(String id) {
        User user = this.modelMapper.map(this.getUserServiceModelById(id), User.class);
//        List<Booking> bookings = user.getBookings();
//        for (Booking booking : bookings) {
//            this.bookingService.deleteBooking(booking);
//        }

        this.userRepository.deleteById(id);
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
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(s).
                orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

}
