package softuni.springadvanced.scheduledTasks;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import softuni.springadvanced.services.UserService;

@Component
@EnableAsync
public class scheduledTasks {

    private final UserService userService;

    public scheduledTasks(UserService userService) {
        this.userService = userService;
    }

    @Async
    @Scheduled(cron = "0 55 7 * * *", zone = "")
    public void addMemberPointsToRegisteredUsers(){
        this.userService.addMemberPointsToUsersInDb();
        this.userService.checkMemberStatusOfUsers();

    }
}
