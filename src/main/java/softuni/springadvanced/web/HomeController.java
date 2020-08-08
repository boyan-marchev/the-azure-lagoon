package softuni.springadvanced.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;



import javax.servlet.http.HttpSession;


@Controller
public class HomeController {

    @GetMapping("/")
    @PreAuthorize("isAnonymous()")
    public String index(Model model) {
        String title = "Index";
        if (!model.containsAttribute(title)){
            model.addAttribute("title", title);
        }

        return "index";
    }

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public String home(Model model){
        String title = "Home";
        if (!model.containsAttribute(title)){
            model.addAttribute("title", title);
        }
        return "home";
    }
}
