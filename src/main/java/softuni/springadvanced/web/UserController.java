package softuni.springadvanced.web;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.springadvanced.models.binding.UserLoginBindingModel;
import softuni.springadvanced.models.binding.UserRegisterBindingModel;
import softuni.springadvanced.models.entity.Roles;
import softuni.springadvanced.models.service.RoleServiceModel;
import softuni.springadvanced.models.service.UserServiceModel;
import softuni.springadvanced.services.RoleService;
import softuni.springadvanced.services.UserService;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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
    public String login(Model model, HttpSession httpSession) {

        if (httpSession.getAttribute("user") == null) {

            if (!model.containsAttribute("userLoginBindingModel")) {
                model.addAttribute("userLoginBindingModel", new UserLoginBindingModel());
            }

            return "login";

        } else {
            return "redirect:/";
        }

    }

//    @PostMapping("/login")
//    public ModelAndView loginPost(@Valid @ModelAttribute("userLoginBindingModel")
//                                          UserLoginBindingModel userLoginBindingModel,
//                                  BindingResult bindingResult,
//                                  ModelAndView modelAndView,
//                                  HttpSession httpSession, RedirectAttributes redirectAttributes) {
//
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute("userLoginBindingModel", userLoginBindingModel);
//            redirectAttributes
//                    .addFlashAttribute("org.springframework.validation.BindingResult.userLoginBindingModel"
//                            , bindingResult);
//            modelAndView.setViewName("redirect:login");
//
//        } else {
//            UserServiceModel userServiceModel = this.userService.getUserServiceModelByUsername(userLoginBindingModel.getUsername());
//
//            if (userServiceModel == null || !userServiceModel.getUsername().equals(userLoginBindingModel.getUsername())
//                    || !userServiceModel.getPassword().equals(userLoginBindingModel.getPassword())) {
//
//                redirectAttributes.addFlashAttribute("isFound", false);
//                redirectAttributes.addFlashAttribute("userLoginBindingModel", userLoginBindingModel);
//
//                modelAndView.setViewName("redirect:login");
//
//            } else {
//
//                httpSession.setAttribute("user", userServiceModel);
//                httpSession.setAttribute("id", userServiceModel.getId());
//                httpSession.setAttribute("username", userServiceModel.getUsername());
//
//                modelAndView.setViewName("redirect:/");
//            }
//        }
//
//        return modelAndView;
//    }

    @GetMapping("/logout")
    public ModelAndView logout(ModelAndView modelAndView) {
        modelAndView.setViewName("redirect:/");

        return modelAndView;

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
            UserServiceModel user =
                    this.modelMapper.map(userRegisterBindingModel, UserServiceModel.class);

            if (user != null &&
                    this.userService.getUserServiceModelByUsername(userRegisterBindingModel.getUsername()) == null) {

                if (this.userService.getAllUsers().size() == 0) {
                    user.setAuthorities(this.roleService.getAllAuthoritiesAsServiceModels());

                } else {
                    user.setAuthorities(new LinkedHashSet<>());
                    user.getAuthorities().add(this.modelMapper.map(this.roleService.getRoleByAuthority(Roles.USER.toString()),
                            RoleServiceModel.class));
                }

                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                this.userService.saveUserInDatabase(user);

                modelAndView.setViewName("redirect:login");

            } else {
                modelAndView.setViewName("redirect:register");

            }

        }

        return modelAndView;
    }
}
