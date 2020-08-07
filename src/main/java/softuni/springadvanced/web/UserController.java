package softuni.springadvanced.web;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.springadvanced.models.binding.UserLoginBindingModel;
import softuni.springadvanced.models.binding.UserRegisterBindingModel;
import softuni.springadvanced.models.entity.Role;
import softuni.springadvanced.models.entity.Roles;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.RoleService;
import softuni.springadvanced.services.UserService;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.LinkedHashSet;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, RoleService roleService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleService = roleService;
    }

    @GetMapping("/login")
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


    @GetMapping("/register")
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
    public String admin() {
        return "admin";
    }

    @PostMapping("/set-role-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView setRoleOfUser(@PathVariable String id, ModelAndView modelAndView) {
        this.changeRoleOfUser(id);
        modelAndView.setViewName("redirect:admin");

        return modelAndView;
    }

    private void changeRoleOfUser(String id) {
        UserServiceModel userServiceModel = this.userService.getUserServiceModelById(id);

        if (userServiceModel.getAuthorities().size() == 2) {
            userServiceModel.getAuthorities().clear();

            Role role = this.roleService.getRoleByAuthority(Roles.USER.toString());

            userServiceModel.getAuthorities().add(this.modelMapper.map(role, RoleServiceModel.class));
            this.userService.saveUserInDatabase(userServiceModel);

        } else {
            userServiceModel.setAuthorities(this.roleService.getAllAuthoritiesAsServiceModels());

        }

        this.userService.saveUserInDatabase(userServiceModel);

    }

}
