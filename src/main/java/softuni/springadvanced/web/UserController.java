package softuni.springadvanced.web;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import softuni.springadvanced.models.entity.Roles;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.FacilityServiceModel;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.models.view.UserChangeRoleViewModel;
import softuni.springadvanced.models.view.UserDeleteViewModel;
import softuni.springadvanced.services.FacilityService;
import softuni.springadvanced.services.RoleService;
import softuni.springadvanced.services.UserService;


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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleService roleService;
    private final FacilityService facilityService;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, RoleService roleService, FacilityService facilityService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleService = roleService;
        this.facilityService = facilityService;
    }

    @GetMapping("/login")
    @PageTitle("Login")
    public String login(@ModelAttribute("userLoginBindingModel")
                                UserLoginBindingModel userLoginBindingModel,
                        Model model, HttpSession httpSession) {

        if (httpSession.getAttribute("user") == null) {

            if (!model.containsAttribute("userLoginBindingModel")) {
                model.addAttribute("userLoginBindingModel", new UserLoginBindingModel());
            }

            return "login";

        } else {
            return "redirect:/";
        }

    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }


    @GetMapping("/register")
    @PageTitle("Register")
    public String register(Model model, HttpSession httpSession) {

        if (httpSession.getAttribute("user") == null) {

            if (!model.containsAttribute("userRegisterBindingModel")) {
                model.addAttribute("userRegisterBindingModel", new UserRegisterBindingModel());
            }

            return "register";

        } else {
            return "redirect:/";
        }


    }

    @PostMapping("/register")
    public ModelAndView registerPost(@Valid @ModelAttribute("userRegisterBindingModel")
                                             UserRegisterBindingModel userRegisterBindingModel,
                                     BindingResult bindingResult,
                                     ModelAndView modelAndView,
                                     RedirectAttributes redirectAttributes) {

        String password = userRegisterBindingModel.getPassword();
        String confirmPassword = userRegisterBindingModel.getConfirmPassword();

        if (bindingResult.hasErrors() || !password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel"
                            , bindingResult);

            modelAndView.setViewName("redirect:register");

        } else {
            UserServiceModel userServiceModel =
                    this.modelMapper.map(userRegisterBindingModel, UserServiceModel.class);

            if (userServiceModel != null &&
                    this.userService.getUserServiceModelByUsername(userRegisterBindingModel.getUsername()) == null) {

                if (this.userService.getAllUsers().size() == 0) {
                    userServiceModel.setAuthorities(this.roleService.getAllAuthoritiesAsServiceModels());

                } else {
                    userServiceModel.setAuthorities(new LinkedHashSet<>());
                    userServiceModel.getAuthorities().add(this.modelMapper.map(this.roleService.getRoleByAuthority(Roles.USER.toString()),
                            RoleServiceModel.class));
                }

                userServiceModel.setPassword(bCryptPasswordEncoder.encode(userServiceModel.getPassword()));
                this.userService.saveUserInDatabase(userServiceModel);

                modelAndView.setViewName("redirect:login");

            } else {
                modelAndView.setViewName("redirect:register");

            }

        }

        return modelAndView;
    }

    @GetMapping("/admin")
    @PageTitle("Admin")
    public String admin() {

        return "admin";
    }

    @GetMapping("/change-role")
    @PageTitle("Change role")
    public ModelAndView changeRole(ModelAndView modelAndView) {

        List<String> allUsernames = this.userService.getAllUsers().stream()
                .map(User::getUsername).collect(Collectors.toList());

        modelAndView.addObject("allUsernames", allUsernames);

        modelAndView.setViewName("change-role");
        return modelAndView;

    }


    @PostMapping("/change-role")
    public ModelAndView changeRolePost(@ModelAttribute("userChangeRoleViewModel")UserChangeRoleViewModel userChangeRoleViewModel,
                                       ModelAndView modelAndView) {

            String username = userChangeRoleViewModel.getUsername();
            this.userService.changeRoleOfUser(username);
            modelAndView.setViewName("redirect:admin");

        return modelAndView;
    }

    @GetMapping("/add-facility")
    @PageTitle("Add facility")
    public String addFacility(Model model) {

        List<String> allFacilityTypes = this.facilityService.getAllFacilityTypes();

        if (!model.containsAttribute("facilityAddBindingModel")) {
            model.addAttribute("facilityAddBindingModel", new FacilityAddBindingModel());
            model.addAttribute("allFacilityTypes", allFacilityTypes);

        }
        return "add-facility";
    }

    @PostMapping("/add-facility")
    public ModelAndView addFacilityPost(@Valid @ModelAttribute("facilityAddBindingModel")
                                                FacilityAddBindingModel facilityAddBindingModel,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("facilityAddBindingModel", facilityAddBindingModel);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.exerciseAddBindingModel"
                            , bindingResult);
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