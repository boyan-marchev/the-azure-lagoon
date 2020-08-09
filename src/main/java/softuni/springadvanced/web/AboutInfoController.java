package softuni.springadvanced.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("resort")
public class AboutInfoController {

    @GetMapping("/resort-info")
    @PageTitle("About")
    public String resortInfo(){
        return "resort-info";
    }
}
