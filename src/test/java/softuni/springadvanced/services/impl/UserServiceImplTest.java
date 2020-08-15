package softuni.springadvanced.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.BookingServiceModel;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.repositories.UserRepository;
import softuni.springadvanced.services.RoleService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserServiceImpl service;

    @MockBean
    UserRepository userRepository;

    @Mock
    RoleService roleService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    User user;
    UserServiceModel userServiceModel;

    @BeforeEach
    public void setUp() {
        user = new User();
        userServiceModel = new UserServiceModel();

        user.setFirstName("pesho");

        userServiceModel.setFirstName("gosho");
        userServiceModel.setLastName("goshov");
        userServiceModel.setUsername("gosho");
        userServiceModel.setPassword("1");
        userServiceModel.setEmail("email");
        userServiceModel.setBudget(BigDecimal.valueOf(0));
        userServiceModel.setClubMemberPoints(0);
        userServiceModel.setMemberStatus("ROOKIE");
        userServiceModel.setAuthorities(Set.of(new RoleServiceModel()));
        userServiceModel.setBookings(List.of(new BookingServiceModel()));

    }

    @Test
    void getAllUsers() {
    }

    @Test
    void getUserByLastname() {
    }

    @Test
    void getUserByUsername() {
    }

    @Test
    void getUserServiceModelById() {
    }

    @Test
    public void saveUserInDatabase() {
        Mockito.when(modelMapper.map(userServiceModel, User.class)).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserServiceModel.class)).thenReturn(userServiceModel);

        Mockito.verify(modelMapper).map(userServiceModel, User.class);
        Mockito.verify(modelMapper).map(user, UserServiceModel.class);
//        Mockito.verify(userRepository).saveAndFlush(user);

        assertEquals(0, userRepository.findAll().size());
        service.saveUserInDatabase(userServiceModel);
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void changeRoleOfUser() {
    }

    @Test
    void getAllUsernamesAsString() {
    }

    @Test
    void getAllUsersAsServiceModels() {
    }

    @Test
    void getAllUsersAsViewChangeRoleModels() {
    }

    @Test
    void addMemberPointsToUsersInDb() {
    }

    @Test
    void checkMemberStatusOfUsers() {
    }

    @Test
    void loadUserByUsername() {
    }
}