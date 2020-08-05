package softuni.springadvanced.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import softuni.springadvanced.services.BarService;

@Controller
@RequestMapping("/bars")
public class BarController {

    private final BarService barService;

    @Autowired
    public BarController(BarService barService) {
        this.barService = barService;
    }

    @GetMapping("/bars-info")
    public String info(){
        return "bars-info";
    }
}
