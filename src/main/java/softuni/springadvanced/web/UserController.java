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
import softuni.springadvanced.models.binding.RoleAddBindingModel;
import softuni.springadvanced.models.binding.UserLoginBindingModel;
import softuni.springadvanced.models.binding.UserRegisterBindingModel;
import softuni.springadvanced.models.entity.Roles;
import softuni.springadvanced.models.entity.User;
import softuni.springadvanced.models.service.FacilityServiceModel;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
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
    public String login(@ModelAttribute("userLoginBindingModel")
                                UserLoginBindingModel userLoginBindingModel,
                        Model model, HttpSession httpSession) {

        String title = "Login";
        if (!model.containsAttribute(title)){
            model.addAttribute("title", title);
        }

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

        String title = "Register";
        if (!model.containsAttribute(title)){
            model.addAttribute("title", title);
        }

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
    public String admin(Model model) {
        String title = "Admin";
        if (!model.containsAttribute(title)){
            model.addAttribute("title", title);
        }
        return "admin";
    }

    @GetMapping("/change-role")
    public ModelAndView changeRole(@ModelAttribute("roleAddBindingModel") RoleAddBindingModel roleAddBindingModel,
                                   BindingResult bindingResult, ModelAndView modelAndView) {
        String title = "Set role";
        modelAndView.addObject("title", title);

        List<String> allUsernames = this.userService.getAllUsers().stream()
                .map(User::getUsername).collect(Collectors.toList());

        modelAndView.addObject("allUsernames", allUsernames);

        modelAndView.setViewName("change-role");
        return modelAndView;

    }

//    @PostMapping("/change-role/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ModelAndView setRoleOfUser(@PathVariable String id, ModelAndView modelAndView) {
//        this.changeRoleOfUser(id);
//        modelAndView.setViewName("redirect:admin");
//
//        return modelAndView;
//    }

    @PostMapping("/change-role")
    public ModelAndView changeRolePost(@Valid @ModelAttribute("roleAddBindingModel") RoleAddBindingModel roleAddBindingModel,
                                       BindingResult bindingResult, ModelAndView modelAndView,
                                       RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("roleAddBindingModel", roleAddBindingModel);
            modelAndView.setViewName("redirect:change-role");

        } else {
            String username = roleAddBindingModel.getUsername();
            this.userService.changeRoleOfUser(username);
            modelAndView.setViewName("redirect:admin");

        }

        return modelAndView;
    }

    @GetMapping("/add-facility")
    public String addFacility(Model model) {

        String title = "Add facility";
        if (!model.containsAttribute(title)){
            model.addAttribute("title", title);
        }

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

}