package softuni.springadvanced.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.springadvanced.models.binding.FacilityAddBindingModel;
import softuni.springadvanced.models.binding.UserLoginBindingModel;
import softuni.springadvanced.models.binding.UserRegisterBindingModel;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.FacilityServiceModel;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.models.view.UserChangeRoleViewModel;
import softuni.springadvanced.services.FacilityService;
import softuni.springadvanced.services.RoleService;
import softuni.springadvanced.services.UserService;
import softuni.springadvanced.web.annotations.PageTitle;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final FacilityService facilityService;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, FacilityService facilityService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.facilityService = facilityService;
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    @PageTitle("Login")
    public String login(@ModelAttribute("userLoginBindingModel")
                                UserLoginBindingModel userLoginBindingModel) {

        return "login";
    }

    @GetMapping("/login-error")
    @PreAuthorize("isAnonymous()")
    public String loginError(Model model) {

        model.addAttribute("loginError", true);
        return "login";
    }


    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    @PageTitle("Register")
    public String register(@ModelAttribute("userRegisterBindingModel") UserRegisterBindingModel userRegisterBindingModel) {

        return "register";
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerPost(@Valid @ModelAttribute("userRegisterBindingModel")
                                             UserRegisterBindingModel userRegisterBindingModel,
                                     BindingResult bindingResult,
                                     ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("redirect:register");
        }

        if (!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())){
            modelAndView.setViewName("redirect:register");
        }

        UserServiceModel userServiceModel =
                this.modelMapper.map(userRegisterBindingModel, UserServiceModel.class);

            this.userService.saveUserInDatabase(userServiceModel);
            modelAndView.setViewName("redirect:login");

        return modelAndView;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PageTitle("Admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/change-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PageTitle("Change role")
    public ModelAndView changeRole(ModelAndView modelAndView) {

        List<UserChangeRoleViewModel> users = this.userService.getAllUsersAsViewChangeRoleModels();
        modelAndView.addObject("users", users);

        modelAndView.setViewName("change-role");
        return modelAndView;

    }


    @PostMapping("/change-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView changeRolePost(@ModelAttribute("userChangeRoleViewModel") UserChangeRoleViewModel userChangeRoleViewModel,
                                       BindingResult bindingResult,
                                       ModelAndView modelAndView) {

        if (bindingResult.hasErrors()){
            modelAndView.setViewName("redirect:change-role");
            return modelAndView;
        }

        String[] tokens = userChangeRoleViewModel.getUsername().split(" ");
        String username = tokens[1];

        if (username.equals("User")){
            modelAndView.setViewName("redirect:change-role");
            return modelAndView;
        }
        this.userService.changeRoleOfUser(username);
        modelAndView.setViewName("redirect:admin");

        return modelAndView;
    }

    @GetMapping("/add-facility")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PageTitle("Add facility")
    public ModelAndView addFacility(@ModelAttribute("facilityAddBindingModel") FacilityAddBindingModel facilityAddBindingModel,
                                    ModelAndView modelAndView) {

        List<String> allFacilityTypes = this.facilityService.getAllFacilityTypes();
        modelAndView.addObject("allFacilityTypes", allFacilityTypes);
        modelAndView.setViewName("add-facility");

        return modelAndView;
    }

    @PostMapping("/add-facility")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView addFacilityPost(@Valid @ModelAttribute("facilityAddBindingModel")
                                                FacilityAddBindingModel facilityAddBindingModel,
                                        BindingResult bindingResult,
                                        ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("redirect:add-facility");

        } else {

            FacilityServiceModel facilityServiceModel = this.modelMapper.map(facilityAddBindingModel,
                    FacilityServiceModel.class);

            this.facilityService.saveFacilityInDatabase(facilityServiceModel);
            modelAndView.setViewName("redirect:admin");

        }
        return modelAndView;
    }

    // TODO: 09-Aug-20 Implement and test methods
//    @GetMapping("/delete-user/{id}")
//    @PageTitle("Delete user")
//    public ModelAndView deleteUser(ModelAndView modelAndView) {
//
//        List<UserDeleteViewModel> userDeleteViewModels =
//                this.userService.getAllUsersAsServiceModels().stream()
//                        .map(userServiceModel -> this.modelMapper.map(userServiceModel, UserDeleteViewModel.class))
//                        .collect(Collectors.toList());
//
//        modelAndView.addObject("userDeleteViewModels", userDeleteViewModels);
//
//        modelAndView.setViewName("delete-user");
//        return modelAndView;
//
//    }
//
//    @PostMapping("/delete-user/{id}")
//    public ModelAndView deleteUserPost(@PathVariable("id") String id, ModelAndView modelAndView) {
//        this.userService.deleteUserById(id);
//        modelAndView.setViewName("redirect:admin");
//
//        return modelAndView;
//    }

}