package softuni.springadvanced.web.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import softuni.springadvanced.web.annotations.PageTitle;

@Controller
@RequestMapping("resort")
public class AboutInfoController {

    @GetMapping("/resort-info")
    @PreAuthorize("isAnonymous()")
    @PageTitle("About")
    public String resortInfo(){
        return "resort-info";
    }
}
